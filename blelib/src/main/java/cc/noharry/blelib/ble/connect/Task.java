package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
  protected   Type mType;
  protected BluetoothGattService mBluetoothGattService;
  protected BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  protected BluetoothGattDescriptor mBluetoothGattDescriptor;
  protected String mServiceUUID;
  protected String mCharacteristicUUID;
  protected String mDescriptorUUID;
  protected boolean isUseUUID=false;
  protected BleDevice mBleDevice;
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
    WAIT_FOR_VALUE_CHANGE
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

  public static WriteTask newWriteTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,serviceUUID,characteristicUUID,data);
  }

  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,characteristic,data);
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
    callback.onOperationSuccess(bleDevice);
    if(mType==Type.ENABLE_NOTIFICATIONS){
      mBleConnectorProxy.taskNotify(0);
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
