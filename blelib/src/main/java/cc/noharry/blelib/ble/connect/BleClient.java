package cc.noharry.blelib.ble.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.connect.Task.Type;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BaseBleGattCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.exception.GattError;
import cc.noharry.blelib.util.L;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/05/18
 */

public class BleClient implements IBleOperation{
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private BleDevice mBleDevice;
  private BluetoothGatt gatt;
  private BaseBleConnectCallback mBleConnectCallback;
  private AtomicBoolean isConnected=new AtomicBoolean(false);
  private final BleConnectorProxy mBleConnectorProxy;
  private Task mCurrentTask;
  private AtomicBoolean isOperating=new AtomicBoolean(false);
  private final static UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  private WriteTask mCurrentDataChangeTask;
  private int mCurrentConnectionState=BluetoothProfile.STATE_DISCONNECTED;


  public BleClient(BleDevice bleDevice) {
    mBleDevice = bleDevice;
    MultipleBleController.getInstance(BleAdmin.getContext()).getClientMap().put(getKey(),this);
//    BleAdmin.getINSTANCE(BleAdmin.getContext()).getMultipleBleController()
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public String getKey(){
    return mBleDevice.getKey();
  }

  public AtomicBoolean getIsConnected() {
    return isConnected;
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }


  @SuppressLint("NewApi")
  protected synchronized BluetoothGatt connect(boolean isAutoConnect,BaseBleConnectCallback callback){
      return connect(isAutoConnect,BluetoothDevice.PHY_LE_1M_MASK,callback);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  protected synchronized BluetoothGatt connect(boolean isAutoConnect,int preferredPhy,BaseBleConnectCallback callback){
    mBleConnectCallback=callback;
    L.i("connect");
    mBleConnectCallback.onDeviceConnectingBase(getBleDevice());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBaseBleGattCallback, BluetoothDevice.TRANSPORT_LE, preferredPhy, mHandler);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBaseBleGattCallback, BluetoothDevice.TRANSPORT_LE);
    } else {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBaseBleGattCallback);
    }
    return gatt;
  }

  protected synchronized void disconnect(){
    if (gatt!=null){
      gatt.disconnect();
      mBleConnectCallback.onDeviceDisconnectingBase(getBleDevice());
      L.i("disconnect():"+mBleDevice);
    }
  }

  @Override
  public String toString() {
    return "BleClient{" +
        "mBleDevice=" + mBleDevice +
        ", gatt=" + gatt +
        ", isConnected=" + isConnected.get() +
        '}';
  }

  private BaseBleGattCallback mBaseBleGattCallback =new BaseBleGattCallback() {
    @Override
    public void onConnectionStateChangeMain(BluetoothGatt gatt, int status, int newState) {
      mBleConnectCallback.onConnectionStateChangeBase(getBleDevice(),gatt,status,newState);
      mCurrentConnectionState=newState;
      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){
        isConnected.set(true);
        BleClient.this.gatt.discoverServices();
        mBleConnectorProxy.taskNotify(status);
        mBleConnectCallback.onDeviceConnectedBase(getBleDevice());
      }
      if (newState==BluetoothProfile.STATE_DISCONNECTED){
        isConnected.set(false);
        mBleConnectorProxy.taskNotify(status);
        mBleConnectCallback.onDeviceDisconnectedBase(getBleDevice(),status);
      }
      if (GattError.isConnectionError(status)){
        handleConnStatu(status);
      }else {
        handleGattStatu(status);
      }
//      L.i("onConnectionStateChangeMain"+" statu:"+status+" newState:"+newState);
    }

    @Override
    public void onServicesDiscoveredMain(BluetoothGatt gatt, int status) {
      mBleConnectCallback.onServicesDiscoveredBase(getBleDevice(),gatt,status);
      /*for (BluetoothGattService service:gatt.getServices()){
        L.i("onServicesDiscoveredMain"+" statu:"+status+" services:"+service.getUuid().toString());
      }*/


    }

    @Override
    public void onDescriptorReadMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      mBleConnectCallback.onDescriptorReadBase(getBleDevice(),gatt,descriptor,status);
//      L.i("onDescriptorReadMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onDescriptorWriteMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      mBleConnectCallback.onDescriptorWriteBase(getBleDevice(),gatt,descriptor,status);
//      L.i("onDescriptorWriteMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onCharacteristicWriteMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      mBleConnectCallback.onCharacteristicWriteBase(getBleDevice(),gatt,characteristic,status);
//      L.i("onCharacteristicWriteMain"+" statu:"+status+" characteristic:"+characteristic);
//      mBleConnectorProxy.taskNotify(status);
      WriteTask writeTask= (WriteTask) mCurrentTask;
      if (GattError.GATT_SUCCESS==status){
        Data data=new Data();
        data.setValue(characteristic.getValue());
        writeTask.notifyDataSent(getBleDevice(),data);
      }else {
        writeTask.notifyError(getBleDevice(),status);
      }

      if (!writeTask.getData().isFinished()){
        handleWrite(characteristic);
      }

    }

    @Override
    public void onCharacteristicReadMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      mBleConnectCallback.onCharacteristicReadBase(getBleDevice(),gatt,characteristic,status);
//      L.i("onCharacteristicReadMain"+" statu:"+status+" characteristic:"+ Arrays.toString(characteristic.getValue()));
//        mBleConnectorProxy.taskNotify(status);
        ReadTask readTask= (ReadTask) mCurrentTask;
        if (GattError.GATT_SUCCESS==status){
          Data data=new Data();
          data.setValue(characteristic.getValue());
          readTask.notifyDataRecived(getBleDevice(),data);
        }else {
          readTask.notifyError(getBleDevice(),status);
        }

    }

    @Override
    public void onCharacteristicChangedMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      mBleConnectCallback.onCharacteristicChangedBase(getBleDevice(),gatt,characteristic);
//      L.i("onCharacteristicChangedMain"+" characteristic:"+Arrays.toString(characteristic.getValue())+" dataTask:"+mCurrentDataChangeTask);
      if(mCurrentDataChangeTask!=null&&mCurrentDataChangeTask.getType()== Type.ENABLE_NOTIFICATIONS){
        Data data=new Data();
        data.setValue(characteristic.getValue());
        mCurrentDataChangeTask.notifyDatachanged(getBleDevice(),data);
      }
    }

    @Override
    public void onReadRemoteRssiMain(BluetoothGatt gatt, int rssi, int status) {
      mBleConnectCallback.onReadRemoteRssiBase(getBleDevice(),gatt,rssi,status);
//      L.i("onReadRemoteRssiMain"+" statu:"+status+" rssi:"+rssi);
    }

    @Override
    public void onReliableWriteCompletedMain(BluetoothGatt gatt, int status) {
      mBleConnectCallback.onReliableWriteCompletedBase(getBleDevice(),gatt,status);
//      L.i("onReliableWriteCompletedMain"+" statu:"+status);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    public void onMtuChangedMain(BluetoothGatt gatt, int mtu, int status) {
      mBleConnectCallback.onMtuChangedBase(getBleDevice(),gatt,mtu,status);
//      L.i("onMtuChangedMain"+" statu:"+status+" mtu:"+mtu);
    }

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    public void onPhyReadMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      mBleConnectCallback.onPhyReadBase(getBleDevice(),gatt,txPhy,rxPhy,status);
//      L.i("onPhyReadMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
    }

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    public void onPhyUpdateMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      mBleConnectCallback.onPhyUpdateBase(getBleDevice(),gatt,txPhy,rxPhy,status);
//      L.i("onPhyUpdateMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
    }
  };

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BaseBleConnectCallback callback) {
    connect(isAutoConnect, callback);
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback) {
    connect(isAutoConnect, preferredPhy, callback);
  }

  @Override
  public void doDisconnect(BleDevice bleDevice) {
    disconnect();
  }

  @Override
  public void doTask(Task task) {
    L.i("doTask");
    mCurrentTask=task;
    if (isConnected.get()&&!isOperating.get()){
      handleTask(task);
    }else {
      mCurrentTask.notifyError(getBleDevice(),GattError.LOCAL_GATT_OPERATION_DEVICE_DISCONNECTED);
//      mBleConnectorProxy.taskNotify(1);
      L.e("Device Disconnected");
    }

  }



  private void handleTask(Task task) {
    BluetoothGattService mBluetoothGattService;
    BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    isOperating.set(true);
    if (mCurrentTask.isUseUUID){
      mBluetoothGattService=gatt.getService(UUID.fromString(mCurrentTask.mServiceUUID));
      mBluetoothGattCharacteristic=mBluetoothGattService.getCharacteristic(UUID.fromString(mCurrentTask.mCharacteristicUUID));
    }else {
      mBluetoothGattService=mCurrentTask.mBluetoothGattService;
      mBluetoothGattCharacteristic=mCurrentTask.mBluetoothGattCharacteristic;
    }
    boolean isOperationSuccess=false;
    switch (mCurrentTask.mType){
      case READ:
        isOperationSuccess= handleRead(mBluetoothGattCharacteristic);
//        L.e("Read:"+isOperationSuccess);
        break;
      case WRITE:
        isOperationSuccess=handleWrite(mBluetoothGattCharacteristic);
        break;
      case ENABLE_NOTIFICATIONS:
        isOperationSuccess=handleEnableNotification(mBluetoothGattCharacteristic);
        break;
      case DISABLE_NOTIFICATIONS:
        isOperationSuccess=handleDisableNotification(mBluetoothGattCharacteristic);
        break;
      default:
    }
    isOperating.set(false);
//    mBleConnectorProxy.taskNotify(0);
    if (isOperationSuccess){
      task.notitySuccess(getBleDevice());
    }else {
      task.notifyError(getBleDevice(),GattError.LOCAL_GATT_OPERATION_FAIL);
    }
  }

  private boolean handleEnableNotification(
      BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    mCurrentDataChangeTask= (WriteTask) mCurrentTask;
    if (gatt==null||mBluetoothGattCharacteristic==null){
      return false;
    }

    //not check permissions because it's always return 0
    //see:https://stackoverflow.com/questions/23674668/android-bluetooth-low-energy-characteristic-getpermissions-returns-0
    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_NOTIFY))==0){
      return false;
    }

    //this method only enable notification localy
    //also need to setting CCCD to ENABLE_NOTIFICATION_VALUE to enable notification on ble peripheral
    //see:https://stackoverflow.com/questions/22817005/why-does-setcharacteristicnotification-not-actually-enable-notifications
    //also see:https://developer.android.com/guide/topics/connectivity/bluetooth-le#notification
    boolean localEnable = gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
    final BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
        .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    boolean remoteEnable=false;
    if (descriptor != null) {
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remoteEnable = gatt.writeDescriptor(descriptor);
      L.i("Enable notifications for " + mBluetoothGattCharacteristic.getUuid());
    }
    L.i("result:"+" localEnable:"+localEnable+" remoteEnable:"+remoteEnable+" descriptor:"+descriptor);
    boolean result = localEnable & remoteEnable;
    return result;
  }



  private boolean handleDisableNotification(
      BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    mCurrentDataChangeTask= (WriteTask) mCurrentTask;
    if (gatt==null||mBluetoothGattCharacteristic==null){
      return false;
    }

    //not check permissions because it's always return 0
    //see:https://stackoverflow.com/questions/23674668/android-bluetooth-low-energy-characteristic-getpermissions-returns-0
    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_NOTIFY))==0){
      return false;
    }

    //this method only Disable notification localy
    //also need to setting CCCD to DISABLE_NOTIFICATION_VALUE to Disable notification on ble peripheral
    //see:https://stackoverflow.com/questions/22817005/why-does-setcharacteristicnotification-not-actually-enable-notifications
    //also see:https://developer.android.com/guide/topics/connectivity/bluetooth-le#notification
    boolean localDisable = gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, false);
    final BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
        .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    boolean remoteDisable=false;
    if (descriptor != null) {
      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remoteDisable = gatt.writeDescriptor(descriptor);
      L.i("Disable notifications for " + mBluetoothGattCharacteristic.getUuid());
    }
    boolean result = localDisable & remoteDisable;
    return result;
  }

  private boolean handleWrite(BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    if (gatt==null||mBluetoothGattCharacteristic==null){
      return false;
    }
    //not check permissions because it's always return 0
    //see:https://stackoverflow.com/questions/23674668/android-bluetooth-low-energy-characteristic-getpermissions-returns-0
    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_WRITE
        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))==0){
      return false;
    }
    WriteTask writeTask= (WriteTask) mCurrentTask;
    mBluetoothGattCharacteristic.setValue(writeTask.getData().getValue());
    mBluetoothGattCharacteristic.setWriteType(writeTask.getWriteType());
    return gatt.writeCharacteristic(mBluetoothGattCharacteristic);
  }

  private boolean handleRead(BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    if (gatt==null||mBluetoothGattCharacteristic==null){
      return false;
    }
    //not check permissions because it's always return 0
    //see:https://stackoverflow.com/questions/23674668/android-bluetooth-low-energy-characteristic-getpermissions-returns-0
    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&BluetoothGattCharacteristic.PROPERTY_READ)==0){
      return false;
    }
    return gatt.readCharacteristic(mBluetoothGattCharacteristic);
  }

  private void handleConnStatu(int statuCode){
    L.e("handleConnStatu:"+ GattError.parseConnectionError(statuCode));
  }

  private void handleGattStatu(int statuCode){
    L.e("handleGATTStatu:"+ GattError.parse(statuCode));
  }

  protected int getCurrentConnectionState() {
    return mCurrentConnectionState;
  }

  protected void setIsConnected(boolean isConnected) {
    this.isConnected.set(isConnected);
  }

  protected void setCurrentConnectionState(int currentConnectionState) {
    mCurrentConnectionState = currentConnectionState;
  }
}
