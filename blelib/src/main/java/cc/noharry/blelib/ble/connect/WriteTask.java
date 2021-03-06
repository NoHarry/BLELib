package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.DataChangeCallback;
import cc.noharry.blelib.callback.WriteCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.data.WriteData;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/05/29
 */

public class WriteTask extends Task {

  private WriteCallback mWriteCallback;
  private WriteData data;
  private BleConnectorProxy mBleConnectorProxy;
  private int mWriteType;
  private DataChangeCallback mDataChangeCallback;
  private long mTaskTimeOut = NO_TIME_OUT;

  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic,WriteData data) {
    this(type, bleDevice, bluetoothGattCharacteristic,data,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
  }
  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    this(type, bleDevice, bluetoothGattCharacteristic,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
  }
  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor,WriteData data) {
    this(type, bleDevice,  bluetoothGattDescriptor,data,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
  }

  protected WriteTask(Type type, BleDevice bleDevice, String serviceUUID,
      String characteristicUUID,WriteData data) {
    this(type, bleDevice, serviceUUID, characteristicUUID,data,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
  }

  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic,WriteData data,int writeType) {
    super(type, bleDevice, bluetoothGattCharacteristic);
    this.data=data;
    this.mWriteType=writeType;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }
  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic,int writeType) {
    super(type, bleDevice, bluetoothGattCharacteristic);
    this.mWriteType=writeType;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor,WriteData data,int writeType) {
    super(type, bleDevice,  bluetoothGattDescriptor);
    this.data=data;
    this.mWriteType=writeType;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected WriteTask(Type type, BleDevice bleDevice, String serviceUUID,
      String characteristicUUID,WriteData data,int writeType) {
    super(type, bleDevice, serviceUUID, characteristicUUID);
    this.data=data;
    this.mWriteType=writeType;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected WriteTask(Type type, BleDevice bleDevice, String serviceUUID,
      String characteristicUUID) {
    super(type, bleDevice, serviceUUID, characteristicUUID);
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public WriteTask with(WriteCallback callback) {
    mWriteCallback=callback;
    return this;
  }

  public WriteTask with(DataChangeCallback callback){
    mDataChangeCallback=callback;
    return this;
  }

  @Override
  public WriteTask setTaskTimeOut(long taskTimeOut){
    mTaskTimeOut=taskTimeOut;
    return this;
  }

  @Override
  protected void notifyError(BleDevice bleDevice, int statuCode) {
    mBleConnectorProxy.taskNotify(statuCode);
    if (mWriteCallback!=null){
      mWriteCallback.onFail(bleDevice, statuCode, GattError.parse(statuCode));
      mWriteCallback.onComplete(bleDevice);
    }
    if (mDataChangeCallback!=null){
      mDataChangeCallback.onFail(bleDevice, statuCode, GattError.parse(statuCode));
      mDataChangeCallback.onComplete(bleDevice);
    }


  }

  @Override
  protected void notitySuccess(BleDevice bleDevice) {
    if(mType==Type.ENABLE_NOTIFICATIONS){
      mBleConnectorProxy.taskNotify(0);
    }
    if (mWriteCallback!=null){
      mWriteCallback.onOperationSuccess(bleDevice);
      if(mType==Type.ENABLE_NOTIFICATIONS){
        mWriteCallback.onComplete(bleDevice);
      }
    }

    if (mDataChangeCallback!=null){
      mDataChangeCallback.onOperationSuccess(bleDevice);
      if(mType==Type.ENABLE_NOTIFICATIONS){
        mDataChangeCallback.onComplete(bleDevice);
      }
    }

  }

  protected void notifyDataSent(BleDevice bleDevice,Data data){
    if (this.data.isFinished()){
      mBleConnectorProxy.taskNotify(0);
    }

    if (mWriteCallback!=null){
      mWriteCallback.onDataSent(bleDevice, data,this.data.getTotalPackSize(),this.data.getReamainPackSize());
      if (this.data.isFinished()){
        mWriteCallback.onComplete(bleDevice);
      }

    }

  }

  protected void notifyDatachanged(BleDevice bleDevice,Data data){
    if (mDataChangeCallback!=null){
      mDataChangeCallback.onDataChange(bleDevice, data);
    }
  }

  public WriteData getData() {
    return data;
  }

  public int getWriteType() {
    return mWriteType;
  }

  @Override
  public long getTaskTimeOut() {
    return mTaskTimeOut;
  }
}
