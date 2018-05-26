package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import cc.noharry.blelib.data.BleDevice;

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
  public BleDevice mBleDevice;

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

  public Task(Type type, BleDevice bleDevice,BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    mType = type;
    mBleDevice=bleDevice;
    mBluetoothGattService = bluetoothGattService;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    isUseUUID=false;
  }

  public Task(Type type, BleDevice bleDevice,BluetoothGattCharacteristic bluetoothGattCharacteristic,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    mType = type;
    mBleDevice=bleDevice;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    isUseUUID=false;
  }

  public Task(Type type, BleDevice bleDevice,String serviceUUID, String characteristicUUID) {
    mType = type;
    mBleDevice=bleDevice;
    mServiceUUID = serviceUUID;
    mCharacteristicUUID = characteristicUUID;
    isUseUUID=true;
  }



  public static ReadTask newReadTask(BleDevice bleDevice,String serviceUUID, String characteristicUUID){
    return new ReadTask(Type.READ,bleDevice,serviceUUID,characteristicUUID);
  }

  public static ReadTask newReadTask(BleDevice bleDevice,BluetoothGattService bluetoothGattService,
      BluetoothGattCharacteristic bluetoothGattCharacteristic){
    return new ReadTask(Type.READ,bleDevice,bluetoothGattService,bluetoothGattCharacteristic);
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }

  public Type getType() {
    return mType;
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
