package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public class ReadTask extends Task {
  private BleDevice mBleDevice;
  private BluetoothGattService mBluetoothGattService;
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  private String mServiceUUID;
  private String mCharacteristicUUID;
  private Type mType;


  public ReadTask(Type type, BleDevice bleDevice,BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    super(type, bleDevice,bluetoothGattService, bluetoothGattCharacteristic);
  }

  public ReadTask(Type type, BleDevice bleDevice,BluetoothGattCharacteristic bluetoothGattCharacteristic,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    super(type, bleDevice,bluetoothGattCharacteristic, bluetoothGattDescriptor);
  }

  public ReadTask(Type type, BleDevice bleDevice,String serviceUUID, String characteristicUUID) {
    super(type, bleDevice,serviceUUID, characteristicUUID);
  }
}
