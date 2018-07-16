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
import cc.noharry.blelib.util.MethodUtils;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
  private ScheduledThreadPoolExecutor mTimeOutService;
  private long mLocalTimeOut;
  private boolean isTimeOutMode=false;


  public BleClient(BleDevice bleDevice) {
    mBleDevice = bleDevice;
    MultipleBleController.getInstance(BleAdmin.getContext()).getClientMap().put(getKey(),this);
//    BleAdmin.getINSTANCE(BleAdmin.getContext()).getMultipleBleController()
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()==Looper.getMainLooper()){
      runnable.run();
    }else {
      mHandler.post(runnable);
    }
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



  protected synchronized boolean refreshCache() {
    try {
      final Method refresh = BluetoothGatt.class.getMethod("refresh");
      if (refresh != null) {
        boolean success = (Boolean) refresh.invoke(gatt);
        L.i("refreshDeviceCache, is success:  " + success);
        return success;
      }
    } catch (Exception e) {
      L.e("exception occur while refreshing device: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  protected synchronized void disconnect(){
    if (gatt!=null){
      gatt.disconnect();
      mBleConnectCallback.onDeviceDisconnectingBase(getBleDevice());
      L.i("disconnect():"+mBleDevice);
    }
  }

  private synchronized void disconnectByTimeout(){
    if (gatt!=null){
      gatt.disconnect();
      gatt.close();
      mBleConnectCallback.onDeviceDisconnectedBase(getBleDevice(),-3);
      mBleConnectorProxy.connectionNotify(-3);
      L.e("disconnectByTimeout():"+mBleDevice);;
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
        refreshCache();
        BleClient.this.gatt.discoverServices();
        mBleConnectorProxy.taskNotify(status);
        mBleConnectCallback.onDeviceConnectedBase(getBleDevice());
        mBleConnectorProxy.connectionNotify(status);
        stopTimeTask();
      }
      if (newState==BluetoothProfile.STATE_DISCONNECTED){
        isConnected.set(false);
        mBleConnectorProxy.taskNotify(status);
        mBleConnectCallback.onDeviceDisconnectedBase(getBleDevice(),status);
        gatt.close();
        mBleConnectorProxy.connectionNotify(status);
        stopTimeTask();
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
      ReadTask readTask= (ReadTask) mCurrentTask;
      if (GattError.GATT_SUCCESS==status){
        Data data=new Data();
        data.setValue(descriptor.getValue());
        readTask.notifyDataRecived(getBleDevice(),data);
      }else {
        readTask.notifyError(getBleDevice(),status);
      }
    }

    @Override
    public void onDescriptorWriteMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      mBleConnectCallback.onDescriptorWriteBase(getBleDevice(),gatt,descriptor,status);
//      L.i("onDescriptorWriteMain"+" statu:"+status+" descriptor:"+descriptor);
      WriteTask writeTask= (WriteTask) mCurrentTask;
      if (GattError.GATT_SUCCESS==status){
        Data data=new Data();
        data.setValue(descriptor.getValue());
        writeTask.notifyDataSent(getBleDevice(),data);
      }else {
        writeTask.notifyError(getBleDevice(),status);
      }

      if (!writeTask.getData().isFinished()){
        handleWriteDescriptor(descriptor);
      }
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
      if (mCurrentTask!=null&&mCurrentTask.getType()==Type.CHANGE_MTU){
        MtuTask task= (MtuTask) mCurrentTask;
        if (status==BluetoothGatt.GATT_SUCCESS){
          task.notifyMtuChanged(getBleDevice(),mtu);
        }else {
          task.notifyError(getBleDevice(),status);
        }
      }
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

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    public void onConnectionUpdatedMain(BluetoothGatt gatt, int interval, int latency, int timeout,
        int status) {
      if (mCurrentTask!=null&&mCurrentTask.getType()==Type.CHANGE_CONNECTION_PRIORITY){
        ConnectionPriorityTask task= (ConnectionPriorityTask) mCurrentTask;
        task.notifyConnectionUpdated(getBleDevice(),interval,latency,timeout,status);
      }
    }
  };

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BaseBleConnectCallback callback) {
    connect(isAutoConnect, callback);
  }

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BaseBleConnectCallback callback,
      long timeOut) {
    mLocalTimeOut = timeOut;
    startTimeTask();
    connect(isAutoConnect, callback);
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback) {
    connect(isAutoConnect, preferredPhy, callback);
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback, long timeOut) {
    mLocalTimeOut = timeOut;
    startTimeTask();
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



  @SuppressLint("NewApi")
  private void handleTask(Task task) {
    BluetoothGattService mBluetoothGattService;
    BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    BluetoothGattDescriptor mBluetoothGattDescriptor=null;
    isOperating.set(true);
    if (mCurrentTask.isUseUUID){
      mBluetoothGattService=gatt.getService(UUID.fromString(mCurrentTask.mServiceUUID));
      mBluetoothGattCharacteristic=mBluetoothGattService.getCharacteristic(UUID.fromString(mCurrentTask.mCharacteristicUUID));
    }else {
      mBluetoothGattService=mCurrentTask.mBluetoothGattService;
      mBluetoothGattCharacteristic=mCurrentTask.mBluetoothGattCharacteristic;
      mBluetoothGattDescriptor=mCurrentTask.mBluetoothGattDescriptor;
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
      case CHANGE_MTU:
        isOperationSuccess=handleChangeMtu();
        break;
      case READ_DESCRIPTOR:
        isOperationSuccess=handleReadDescriptor(mBluetoothGattDescriptor);
        break;
      case WRITE_DESCRIPTOR:
        isOperationSuccess=handleWriteDescriptor(mBluetoothGattDescriptor);
        break;
      case ENABLE_INDICATIONS:
        isOperationSuccess=handleEnableIndications(mBluetoothGattCharacteristic);
        break;
      case DISABLE_INDICATIONS:
        isOperationSuccess=handleDisableIndications(mBluetoothGattCharacteristic);
        break;
      case CHANGE_CONNECTION_PRIORITY:
        isOperationSuccess=handleConnectionPriority();
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

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private boolean handleConnectionPriority() {
    if (gatt==null){
      return false;
    }
    ConnectionPriorityTask task= (ConnectionPriorityTask) mCurrentTask;
    L.v("request ConnectionPriority:"+task.getConnectionPriority());
    return gatt.requestConnectionPriority(task.getConnectionPriority());
  }

  private boolean handleEnableIndications(
      BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    mCurrentDataChangeTask= (WriteTask) mCurrentTask;
    if (gatt==null || mBluetoothGattCharacteristic==null){
      return false;
    }

    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&BluetoothGattCharacteristic.PROPERTY_INDICATE)==0){
      return false;
    }
    boolean local = gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
    BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
        .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    boolean remote=false;
    if (descriptor!=null){
//      descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
      descriptor.setValue(mCurrentDataChangeTask.getData().getValue());
      int originWriteTye = mBluetoothGattCharacteristic.getWriteType();
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remote = gatt.writeDescriptor(descriptor);
      mBluetoothGattCharacteristic.setWriteType(originWriteTye);
      L.v("Enable Indications for " + mBluetoothGattCharacteristic.getUuid());
    }
    return local&remote;
  }


  private boolean handleDisableIndications(
      BluetoothGattCharacteristic mBluetoothGattCharacteristic) {
    mCurrentDataChangeTask= (WriteTask) mCurrentTask;
    if (gatt==null || mBluetoothGattCharacteristic==null){
      return false;
    }

    int properties = mBluetoothGattCharacteristic.getProperties();
    if ((properties&BluetoothGattCharacteristic.PROPERTY_INDICATE)==0){
      return false;
    }
    boolean local = gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, false);
    BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
        .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    boolean remote=false;
    if (descriptor!=null){
//      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
      descriptor.setValue(mCurrentDataChangeTask.getData().getValue());
      int originWriteTye = mBluetoothGattCharacteristic.getWriteType();
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remote = gatt.writeDescriptor(descriptor);
      mBluetoothGattCharacteristic.setWriteType(originWriteTye);
      L.v("Disable Indications for " + mBluetoothGattCharacteristic.getUuid());
    }
    return local&remote;
  }

  private boolean handleWriteDescriptor(BluetoothGattDescriptor mBluetoothGattDescriptor) {
    WriteTask writeTask= (WriteTask) mCurrentTask;
    if (gatt==null||mBluetoothGattDescriptor==null){
      return false;
    }
    byte[] value = writeTask.getData().getValue();
    mBluetoothGattDescriptor.setValue(value);
    L.v("Write Descriptor:"+MethodUtils.getString(value));
    return gatt.writeDescriptor(mBluetoothGattDescriptor);
  }

  private boolean handleReadDescriptor(BluetoothGattDescriptor mBluetoothGattDescriptor) {
    if (gatt==null||mBluetoothGattDescriptor==null){
      return false;
    }

    L.v("Read Descriptor:"+mBluetoothGattDescriptor.getUuid().toString());

    return gatt.readDescriptor(mBluetoothGattDescriptor);
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private boolean handleChangeMtu() {
    MtuTask mtuTask= (MtuTask) mCurrentTask;
    if (gatt==null){
      return false;
    }
    L.v("Request Change MTU to "+mtuTask.getMtu());
    return gatt.requestMtu(mtuTask.getMtu());
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
//      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      descriptor.setValue(mCurrentDataChangeTask.getData().getValue());
      int originWriteType = mBluetoothGattCharacteristic.getWriteType();
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remoteEnable = gatt.writeDescriptor(descriptor);
      mBluetoothGattCharacteristic.setWriteType(originWriteType);
      L.v("Enable notifications for " + mBluetoothGattCharacteristic.getUuid());
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
//      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
      descriptor.setValue(mCurrentDataChangeTask.getData().getValue());
      mBluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
      remoteDisable = gatt.writeDescriptor(descriptor);
      L.v("Disable notifications for " + mBluetoothGattCharacteristic.getUuid());
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
    gatt.close();
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

  private void startTimeTask(){
    mTimeOutService = new ScheduledThreadPoolExecutor(1);
    mTimeOutService.schedule(()->{
      L.e("startTimeOutTask");
      runOnUiThread(this::disconnectByTimeout);
    },mLocalTimeOut,TimeUnit.MILLISECONDS);
  }

  private void stopTimeTask(){
    if (mTimeOutService!=null){
      L.e("stopTimeOutTask");
      mTimeOutService.shutdownNow();
      mTimeOutService=null;
    }
  }
}
