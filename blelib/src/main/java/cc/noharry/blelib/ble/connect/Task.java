package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

/**
 * @author NoHarry
 * @date 2018/05/24
 */

public class Task<T>{
  public  Type mType;
  public BluetoothGattService mBluetoothGattService;
  public BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  public BluetoothGattDescriptor mBluetoothGattDescriptor;
  public String mServiceUUID;
  public String mCharacteristicUUID;
  public String mDescriptorUUID;
  public boolean isUseUUID=false;

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

  public Task(Type type, BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    mType = type;
    mBluetoothGattService = bluetoothGattService;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    isUseUUID=false;
  }

  public Task(Type type, BluetoothGattCharacteristic bluetoothGattCharacteristic,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    mType = type;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    isUseUUID=false;
  }

  public Task(Type type, String serviceUUID, String characteristicUUID) {
    mType = type;
    mServiceUUID = serviceUUID;
    mCharacteristicUUID = characteristicUUID;
    isUseUUID=true;
  }

  public static ReadTask newReadTask(String serviceUUID, String characteristicUUID){
    return new ReadTask(Type.READ,serviceUUID,characteristicUUID);
  }

  public static ReadTask newReadTask(BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic){
    return new ReadTask(Type.READ,bluetoothGattService,bluetoothGattCharacteristic);
  }

  public Type getType() {
    return mType;
  }
}
