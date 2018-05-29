package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.WriteCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/05/29
 */

public class WriteTask extends Task {

  private WriteCallback mWriteCallback;
  private byte[] data;
  private BleConnectorProxy mBleConnectorProxy;

  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic,byte[] data) {
    super(type, bleDevice, bluetoothGattCharacteristic);
    this.data=data;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected WriteTask(Type type, BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor,byte[] data) {
    super(type, bleDevice,  bluetoothGattDescriptor);
    this.data=data;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected WriteTask(Type type, BleDevice bleDevice, String serviceUUID,
      String characteristicUUID,byte[] data) {
    super(type, bleDevice, serviceUUID, characteristicUUID);
    this.data=data;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public WriteTask with(WriteCallback callback) {
    mWriteCallback=callback;
    return this;
  }

  @Override
  protected void notifyError(BleDevice bleDevice, int statuCode) {
    if (mWriteCallback!=null){
      mWriteCallback.onFail(bleDevice, statuCode, GattError.parse(statuCode));
    }

  }

  @Override
  protected void notityComplete(BleDevice bleDevice) {
    if (mWriteCallback!=null){
      mWriteCallback.onOperationCompleted(bleDevice);
    }

  }

  protected void notifyDataSent(BleDevice bleDevice,Data data){
    if (mWriteCallback!=null){
      mWriteCallback.onDataSent(bleDevice, data);
    }
  }

  public byte[] getData() {
    return data;
  }
}
