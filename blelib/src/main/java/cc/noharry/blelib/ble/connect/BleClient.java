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
import cc.noharry.blelib.ble.MultipleBleController;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BaseBleGattCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.exception.GattError;
import cc.noharry.blelib.util.L;
import java.util.Arrays;
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
      L.i("disconnect():"+MultipleBleController.getInstance(BleAdmin.getContext()).getClientMap());
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
      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){
        mBleConnectCallback.onDeviceConnectedBase(getBleDevice());
        isConnected.set(true);
        BleClient.this.gatt.discoverServices();
        mBleConnectorProxy.taskNotify(status);
      }
      if (newState==BluetoothProfile.STATE_DISCONNECTED){
        isConnected.set(false);
      }
      if (GattError.isConnectionError(status)){
        handleConnStatu(status);
      }else {
        handleGattStatu(status);
      }
      L.i("onConnectionStateChangeMain"+" statu:"+status+" newState:"+newState);
    }

    @Override
    public void onServicesDiscoveredMain(BluetoothGatt gatt, int status) {
      mBleConnectCallback.onServicesDiscoveredBase(getBleDevice(),gatt,status);
      for (BluetoothGattService service:gatt.getServices()){
        L.i("onServicesDiscoveredMain"+" statu:"+status+" services:"+service.getUuid().toString());
      }


    }

    @Override
    public void onDescriptorReadMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      mBleConnectCallback.onDescriptorReadBase(getBleDevice(),gatt,descriptor,status);
      L.i("onDescriptorReadMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onDescriptorWriteMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      mBleConnectCallback.onDescriptorWriteBase(getBleDevice(),gatt,descriptor,status);
      L.i("onDescriptorWriteMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onCharacteristicWriteMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      mBleConnectCallback.onCharacteristicWriteBase(getBleDevice(),gatt,characteristic,status);
      L.i("onCharacteristicWriteMain"+" statu:"+status+" characteristic:"+characteristic);
//      mBleConnectorProxy.taskNotify(status);
      WriteTask readTask= (WriteTask) mCurrentTask;
      if (GattError.GATT_SUCCESS==status){
        Data data=new Data();
        data.setValue(characteristic.getValue());
        readTask.notifyDataSent(getBleDevice(),data);
      }else {
        readTask.notifyError(getBleDevice(),status);
      }
    }

    @Override
    public void onCharacteristicReadMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      mBleConnectCallback.onCharacteristicReadBase(getBleDevice(),gatt,characteristic,status);
      L.i("onCharacteristicReadMain"+" statu:"+status+" characteristic:"+ Arrays.toString(characteristic.getValue()));
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
      L.i("onCharacteristicChangedMain"+" characteristic:"+Arrays.toString(characteristic.getValue()));
    }

    @Override
    public void onReadRemoteRssiMain(BluetoothGatt gatt, int rssi, int status) {
      mBleConnectCallback.onReadRemoteRssiBase(getBleDevice(),gatt,rssi,status);
      L.i("onReadRemoteRssiMain"+" statu:"+status+" rssi:"+rssi);
    }

    @Override
    public void onReliableWriteCompletedMain(BluetoothGatt gatt, int status) {
      mBleConnectCallback.onReliableWriteCompletedBase(getBleDevice(),gatt,status);
      L.i("onReliableWriteCompletedMain"+" statu:"+status);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    public void onMtuChangedMain(BluetoothGatt gatt, int mtu, int status) {
      mBleConnectCallback.onMtuChangedBase(getBleDevice(),gatt,mtu,status);
      L.i("onMtuChangedMain"+" statu:"+status+" mtu:"+mtu);
    }

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    public void onPhyReadMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      mBleConnectCallback.onPhyReadBase(getBleDevice(),gatt,txPhy,rxPhy,status);
      L.i("onPhyReadMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
    }

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    public void onPhyUpdateMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      mBleConnectCallback.onPhyUpdateBase(getBleDevice(),gatt,txPhy,rxPhy,status);
      L.i("onPhyUpdateMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
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
    if (isConnected.get()&&!isOperating.get()){
      handleTask(task);
    }else {
      mBleConnectorProxy.taskNotify(1);
      L.e("设备未连接");
    }

  }



  private void handleTask(Task task) {
    BluetoothGattService mBluetoothGattService;
    BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    mCurrentTask=task;
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
        L.e("Read:"+isOperationSuccess);
        break;
      case WRITE:
        isOperationSuccess=handleWrite(mBluetoothGattCharacteristic);

        break;
      default:
    }
    isOperating.set(false);
    mBleConnectorProxy.taskNotify(0);
    if (isOperationSuccess){
      task.notityComplete(getBleDevice());
    }else {
      task.notifyError(getBleDevice(),GattError.LOCAL_GATT_OPERATION_FAIL);
    }
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
    mBluetoothGattCharacteristic.setValue(writeTask.getData());
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
    L.e("handleStatu:"+ GattError.parse(statuCode));
  }
}
