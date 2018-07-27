package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGatt;
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
  protected long mTaskTimeOut = NO_TIME_OUT;
  private TaskCallback callback;
  private BleConnectorProxy mBleConnectorProxy;
  protected static final long NO_TIME_OUT=-1;



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

  public Task setTaskTimeOut(long taskTimeOut){
    this.mTaskTimeOut=taskTimeOut;
    return this;
  }


  /**
   * Read the value of the specified characteristic
   * @param bleDevice Target device
   * @param serviceUUID UUID of the service where the characteristic is located
   * @param characteristicUUID UUID of the characteristic that needs to be read
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static ReadTask newReadTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    return new ReadTask(Type.READ,bleDevice,serviceUUID,characteristicUUID);
  }

  /**
   * Read the value of the specified characteristic
   * @param bleDevice Target device
   * @param bluetoothGattCharacteristic The characteristic that needs to be read
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static ReadTask newReadTask(BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic){
    return new ReadTask(Type.READ,bleDevice,bluetoothGattCharacteristic);
  }

  /**
   * Read the value of the specified descriptor
   * @param bleDevice Target device
   * @param bluetoothGattDescriptor The descriptor that needs to be read
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static ReadTask newReadTask(BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor){
    return new ReadTask(Type.READ_DESCRIPTOR,bleDevice,bluetoothGattDescriptor);
  }

  /**
   * Write the value to the specified characteristic
   * @param bleDevice Target device
   * @param serviceUUID UUID of the service where the characteristic is located
   * @param characteristicUUID UUID of the characteristic that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,serviceUUID,characteristicUUID,data);
  }

  /**
   * Write the value to the specified characteristic
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic,WriteData data){
    return new WriteTask(Type.WRITE,bleDevice,characteristic,data);
  }

  /**
   * Write the value to the specified descriptor
   * @param bleDevice Target device
   * @param bluetoothGattDescriptor The descriptor that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattDescriptor bluetoothGattDescriptor,WriteData data){
    return new WriteTask(Type.WRITE_DESCRIPTOR,bleDevice,bluetoothGattDescriptor,data);
  }

  /**
   * Write the value to the specified descriptor
   * @param bleDevice Target device
   * @param serviceUUID UUID of the service where the characteristic is located
   * @param characteristicUUID UUID of the characteristic that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @param writeType The write type to for this characteristic. Can be one
   *                  of :{@link BluetoothGattCharacteristic#WRITE_TYPE_DEFAULT},
   *                  {@link BluetoothGattCharacteristic#WRITE_TYPE_NO_RESPONSE},
   *                  {@link BluetoothGattCharacteristic#WRITE_TYPE_SIGNED}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID,WriteData data,int writeType){
    return new WriteTask(Type.WRITE,bleDevice,serviceUUID,characteristicUUID,data,writeType);
  }

  /**
   * Write the value to the specified descriptor
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @param writeType The write type to for this characteristic. Can be one
   *                  of :{@link BluetoothGattCharacteristic#WRITE_TYPE_DEFAULT},
   *                  {@link BluetoothGattCharacteristic#WRITE_TYPE_NO_RESPONSE},
   *                  {@link BluetoothGattCharacteristic#WRITE_TYPE_SIGNED}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic,WriteData data,int writeType){
    return new WriteTask(Type.WRITE,bleDevice,characteristic,data,writeType);
  }

  /**
   * Enable notifications for a given characteristic.
   * @param bleDevice Target device
   * @param serviceUUID UUID of the service where the characteristic is located
   * @param characteristicUUID UUID of the characteristic that needs to enable notifications
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newEnableNotificationsTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    WriteData data=new WriteData();
    data.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    return new WriteTask(Type.ENABLE_NOTIFICATIONS,bleDevice,serviceUUID,characteristicUUID,data);
  }

  /**
   * Enable notifications for a given characteristic.
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to enable notifications
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newEnableNotificationsTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    WriteData data=new WriteData();
    data.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    return new WriteTask(Type.ENABLE_NOTIFICATIONS,bleDevice,characteristic,data);
  }

  /**
   * Disable notifications for a given characteristic.
   * @param bleDevice Target device
   * @param serviceUUID UUID of the service where the characteristic is located
   * @param characteristicUUID UUID of the characteristic that needs to disable notifications
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newDisableNotificationsTask(BleDevice bleDevice,String serviceUUID
      , String characteristicUUID){
    WriteData data=new WriteData();
    data.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    return new WriteTask(Type.DISABLE_NOTIFICATIONS,bleDevice,serviceUUID,characteristicUUID,data);
  }

  /**
   * Disable notifications for a given characteristic.
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to disable notifications
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newDisableNotificationsTask(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    WriteData data=new WriteData();
    data.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    return new WriteTask(Type.DISABLE_NOTIFICATIONS,bleDevice,characteristic,data);
  }

  /**
   * Read the value of the specified descriptor
   * @param bleDevice Target device
   * @param descriptor The descriptor that needs to be read
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static ReadTask newReadDescriptor(BleDevice bleDevice,BluetoothGattDescriptor descriptor){
    return new ReadTask(Type.READ_DESCRIPTOR,bleDevice,descriptor);
  }

  /**
   * Write the value to the specified descriptor
   * @param bleDevice Target device
   * @param descriptor The descriptor that needs to be written
   * @param data Data object to be written, specify data with {@link WriteData#setValue}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newWriteDescriptor(BleDevice bleDevice,BluetoothGattDescriptor descriptor
      ,WriteData data){
    return new WriteTask(Type.WRITE_DESCRIPTOR,bleDevice,descriptor,data);
  }

  /**
   * Enable indication for a given characteristic
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to enable indication
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newEnableIndication(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.ENABLE_INDICATIONS,bleDevice,characteristic);
  }

  /**
   * Disable indication for a given characteristic.
   * @param bleDevice Target device
   * @param characteristic The characteristic that needs to disable indication
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  public static WriteTask newDisableIndication(BleDevice bleDevice
      ,BluetoothGattCharacteristic characteristic){
    return new WriteTask(Type.DISABLE_INDICATIONS,bleDevice,characteristic);
  }

  /**
   * Request a new MTU
   * @param bleDevice tatget Device
   * @param mtu the New MTU,range between 23 and 517
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  public static MtuTask newMtuTask(BleDevice bleDevice,int mtu){
    return new MtuTask(Type.CHANGE_MTU,bleDevice,mtu);
  }

  /**
   *  Change connection priority
   * @param bleDevice target BLE device
   * @param connectionPriority one of
   *  {@link BluetoothGatt#CONNECTION_PRIORITY_HIGH}
   * ,{@link BluetoothGatt#CONNECTION_PRIORITY_BALANCED}
   * ,{@link BluetoothGatt#CONNECTION_PRIORITY_LOW_POWER}
   * @return A new task that can be enqueued by {@link BleAdmin#addTask(Task)}
   */
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

  public long getTaskTimeOut() {
    return mTaskTimeOut;
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
