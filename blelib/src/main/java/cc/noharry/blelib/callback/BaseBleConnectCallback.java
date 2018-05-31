package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public abstract class BaseBleConnectCallback {
  public abstract void onConnectionStateChangeBase(BleDevice bleDevice,BluetoothGatt gatt, int status, int newState);
  public abstract void onServicesDiscoveredBase(BleDevice bleDevice,BluetoothGatt gatt, int status);
  public abstract void onDescriptorReadBase(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
  public abstract void onDescriptorWriteBase(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
      int status);
  public abstract void onCharacteristicWriteBase(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicReadBase(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicChangedBase(BleDevice bleDevice,BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic);
  public abstract void onReadRemoteRssiBase(BleDevice bleDevice,BluetoothGatt gatt, int rssi, int status);
  public abstract void onReliableWriteCompletedBase(BleDevice bleDevice,BluetoothGatt gatt, int status);
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public abstract void onMtuChangedBase(BleDevice bleDevice,BluetoothGatt gatt, int mtu, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyReadBase(BleDevice bleDevice,BluetoothGatt gatt, int txPhy, int rxPhy, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyUpdateBase(BleDevice bleDevice,BluetoothGatt gatt, int txPhy, int rxPhy, int status);

  public abstract void onDeviceConnectingBase(BleDevice bleDevice);
  public abstract void onDeviceConnectedBase(BleDevice bleDevice);
  public abstract void onDeviceDisconnectingBase(BleDevice bleDevice);
  public abstract void onDeviceDisconnectedBase(BleDevice bleDevice,int status);

}
