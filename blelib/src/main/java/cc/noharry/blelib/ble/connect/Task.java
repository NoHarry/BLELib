package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.TaskCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.WriteData;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/05/24
 */

public class Task{
  protected Type mType;
  protected BluetoothGattService mBluetoothGattService;
  protected BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  protected BluetoothGattDescriptor mBluetoothGattDescriptor;
  protected String mServiceUUID;
  protected String mCharacteristicUUID;
  protected String mDescriptorUUID;
  protected boolean isUseUUID=false;
  protected BleDevice mBleDevice;
  protected int mMtu;
  private TaskCallback callback;
  private BleConnectorProxy mBleConnectorProxy;

  enum Type{
    WRITE,
    READ,
    WRITE_DESCRIPTOR,
    READ_DESCRIPTOR,
    ENABLE_NOTIFICATIONS,
    ENABLE_INDICATIONS,
    DISABLE_NOTIFICATIONS,
    DISABLE_INDICATIONS,
    CHANGE_MTU,
    CHANGE_CONNECTION_PRIORITY
  }

  protected Task(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    mType = type;
    mBleDevice=bleDevice;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    isUseUUID=false;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }


  protected Task(Type type, BleDevice bleDevice,
      int mtu) {
    mType = type;
    mBleDevice=bleDevice;
    isUseUUID=false;
    mMtu=mtu;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected Task(Type type, BleDevice bleDevice) {
    mType = type;
    mBleDevice=bleDevice;
    isUseUUID=false;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected Task(Type type, BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    mType = type;
    mBleDevice=bleDevice;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    isUseUUID=false;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected Task(Type type, BleDevice bleDevice,String serviceUUID, String characteristicUUID) {
    mType = type;
    mBleDevice=bleDevice;
    mServiceUUID = serviceUUID;
    mCharacteristicUUID = characteristicUUID;
    isUseUUID=true;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public Task with(TaskCallback callback){
    this.callback=callback;
    return this;
  }



  public static ReadTask newReadTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    return new ReadTask(Type.READ,bleDevice,serviceUUID,characteristicUUID);
  }

  public static ReadTask newReadTask(BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic){
    return new ReadTask(Type.READ,bleDevice,bluetoothGattCharacteristic);
  }

  public static ReadTask newReadTask(BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor){
    return new ReadTask(Type.READ_DESCRIPTOR,bleDevice,bluetoothGattDescriptor);
  }

  public static WriteTask newWriteTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,serviceUUID,characteristicUUID,data);
  }

  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,characteristic,data);
  }

  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattDescriptor bluetoothGattDescriptor,WriteData data){
    return new WriteTask(Type.WRITE_DESCRIPTOR,bleDevice,bluetoothGattDescriptor,data);
  }

  public static WriteTask newWriteTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID,WriteData data,int writeType){
    return new WriteTask(Type.WRITE,bleDevice,serviceUUID,characteristicUUID,data,writeType);
  }

  public static WriteTask newEnableNotificationsTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    return new WriteTask(Type.ENABLE_NOTIFICATIONS,bleDevice,serviceUUID,characteristicUUID);
  }

  public static WriteTask newEnableNotificationsTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.ENABLE_NOTIFICATIONS,bleDevice,characteristic);
  }

  public static WriteTask newDisableNotificationsTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    return new WriteTask(Type.DISABLE_NOTIFICATIONS,bleDevice,serviceUUID,characteristicUUID);
  }

  public static WriteTask newDisableNotificationsTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.DISABLE_NOTIFICATIONS,bleDevice,characteristic);
  }

  public static ReadTask newReadDescriptor(BleDevice bleDevice,BluetoothGattDescriptor descriptor){
    return new ReadTask(Type.READ_DESCRIPTOR,bleDevice,descriptor);
  }

  public static WriteTask newWriteDescriptor(BleDevice bleDevice,BluetoothGattDescriptor descriptor
      ,WriteData data){
    return new WriteTask(Type.WRITE_DESCRIPTOR,bleDevice,descriptor,data);
  }

  public static WriteTask newEnableIndication(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.ENABLE_INDICATIONS,bleDevice,characteristic);
  }

  public static WriteTask newDisableIndication(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.DISABLE_INDICATIONS,bleDevice,characteristic);
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  public static MtuTask newMtuTask(BleDevice bleDevice,int mtu){
    return new MtuTask(Type.CHANGE_MTU,bleDevice,mtu);
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  public static ConnectionPriorityTask newConnectionPriorityTask(BleDevice bleDevice,int connectionPriority){
    return new ConnectionPriorityTask(Type.CHANGE_CONNECTION_PRIORITY,bleDevice,connectionPriority);
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }

  public Type getType() {
    return mType;
  }

  public TaskCallback getCallback() {
    return callback;
  }

  protected void notitySuccess(BleDevice bleDevice){
    mBleConnectorProxy.taskNotify(0);
    callback.onOperationSuccess(bleDevice);
    if(mType==Type.ENABLE_NOTIFICATIONS||mType==Type.CHANGE_CONNECTION_PRIORITY){
      callback.onComplete(bleDevice);
    }
  }

  protected void notifyError(BleDevice bleDevice,int statuCode){
    mBleConnectorProxy.taskNotify(statuCode);
    callback.onFail(bleDevice,statuCode, GattError.parse(statuCode));
    callback.onComplete(bleDevice);

  }

  @Override
  public String toString() {
    return "Task{" +
        "mType=" + mType +
        ", mBluetoothGattService=" + mBluetoothGattService +
        ", mBluetoothGattCharacteristic=" + mBluetoothGattCharacteristic +
        ", mBluetoothGattDescriptor=" + mBluetoothGattDescriptor +
        ", mServiceUUID='" + mServiceUUID + '\'' +
        ", mCharacteristicUUID='" + mCharacteristicUUID + '\'' +
        ", mDescriptorUUID='" + mDescriptorUUID + '\'' +
        ", isUseUUID=" + isUseUUID +
        ", mBleDevice=" + mBleDevice +
        '}';
  }
}
