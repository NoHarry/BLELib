package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public class ReadTask extends Task {
  private BluetoothGattService mBluetoothGattService;
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  private String mServiceUUID;
  private String mCharacteristicUUID;
  private Type mType;


  public ReadTask(Type type, BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    super(type, bluetoothGattService, bluetoothGattCharacteristic);
  }

  public ReadTask(Type type, BluetoothGattCharacteristic bluetoothGattCharacteristic,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    super(type, bluetoothGattCharacteristic, bluetoothGattDescriptor);
  }

  public ReadTask(Type type, String serviceUUID, String characteristicUUID) {
    super(type, serviceUUID, characteristicUUID);
  }
}
