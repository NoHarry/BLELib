package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.ReadCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public class ReadTask extends Task {
  private ReadCallback mReadCallback;
  private BleConnectorProxy mBleConnectorProxy;


  protected ReadTask(Type type, BleDevice bleDevice,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    super(type, bleDevice, bluetoothGattCharacteristic);
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected ReadTask(Type type, BleDevice bleDevice,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    super(type, bleDevice, bluetoothGattDescriptor);
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  protected ReadTask(Type type, BleDevice bleDevice,String serviceUUID, String characteristicUUID) {
    super(type, bleDevice,serviceUUID, characteristicUUID);
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public ReadTask with(ReadCallback callback){
    mReadCallback=callback;
    return this;
  }

  protected void notifyDataRecived(BleDevice bleDevice,Data data){
    mBleConnectorProxy.taskNotify(0);
    if (mReadCallback!=null){
      mReadCallback.onDataRecived(bleDevice, data);
      mReadCallback.onComplete(bleDevice);
    }

  }

  @Override
  protected void notitySuccess(BleDevice bleDevice) {
    if (mReadCallback!=null){
      mReadCallback.onOperationSuccess(bleDevice);
    }

  }

  @Override
  protected void notifyError(BleDevice bleDevice, int statuCode) {
    mBleConnectorProxy.taskNotify(statuCode);
    if (mReadCallback!=null){

      mReadCallback.onFail(bleDevice, statuCode, GattError.parse(statuCode));
      mReadCallback.onComplete(bleDevice);
    }

  }
}
