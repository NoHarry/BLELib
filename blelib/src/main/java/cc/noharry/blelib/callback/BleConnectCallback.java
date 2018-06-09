package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;
import android.os.Looper;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public abstract class BleConnectCallback extends BaseBleConnectCallback {
  private Handler mHandler=new Handler(Looper.getMainLooper());

  public void setHandler(Handler handler) {
    mHandler = handler;
  }

  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()!=Looper.getMainLooper()){
      mHandler.post(runnable);
    }else {
      runnable.run();
    }
  }

  public abstract void onDeviceConnecting(BleDevice bleDevice);
  public abstract void onDeviceConnected(BleDevice bleDevice);
  public abstract void onServicesDiscovered(BleDevice bleDevice,BluetoothGatt gatt, int status);
  public abstract void onDeviceDisconnecting(BleDevice bleDevice);
  public abstract void onDeviceDisconnected(BleDevice bleDevice,int status);




  @Override
  public void onConnectionStateChangeBase(BleDevice bleDevice, BluetoothGatt gatt, int status,
      int newState) {

  }

  @Override
  public void onServicesDiscoveredBase(BleDevice bleDevice, BluetoothGatt gatt, int status) {
    runOnUiThread(()->onServicesDiscovered(bleDevice, gatt, status));
  }

  @Override
  public void onDescriptorReadBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattDescriptor descriptor, int status) {

  }

  @Override
  public void onDescriptorWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattDescriptor descriptor, int status) {

  }

  @Override
  public void onCharacteristicWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic, int status) {

  }

  @Override
  public void onCharacteristicReadBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic, int status) {

  }

  @Override
  public void onCharacteristicChangedBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic) {

  }

  @Override
  public void onReadRemoteRssiBase(BleDevice bleDevice, BluetoothGatt gatt, int rssi, int status) {

  }

  @Override
  public void onReliableWriteCompletedBase(BleDevice bleDevice, BluetoothGatt gatt, int status) {

  }

  @Override
  public void onMtuChangedBase(BleDevice bleDevice, BluetoothGatt gatt, int mtu, int status) {

  }

  @Override
  public void onPhyReadBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
      int status) {

  }

  @Override
  public void onPhyUpdateBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
      int status) {

  }

  @Override
  public void onDeviceConnectingBase(BleDevice bleDevice) {
    runOnUiThread(()->onDeviceConnecting(bleDevice));
  }

  @Override
  public void onDeviceConnectedBase(BleDevice bleDevice) {
    runOnUiThread(()->onDeviceConnected(bleDevice));
  }

  @Override
  public void onDeviceDisconnectingBase(BleDevice bleDevice) {
    runOnUiThread(()->onDeviceDisconnecting(bleDevice));
  }

  @Override
  public void onDeviceDisconnectedBase(BleDevice bleDevice,int status) {
    runOnUiThread(()->onDeviceDisconnected(bleDevice,status));
  }


}
