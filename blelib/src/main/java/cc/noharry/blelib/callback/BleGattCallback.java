package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/28
 */
 public abstract class BleGattCallback extends BaseBleConnectCallback {
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


  public abstract void onConnectionStateChange(BleDevice bleDevice,BluetoothGatt gatt, int status, int newState);
  public abstract void onServicesDiscovered(BleDevice bleDevice,BluetoothGatt gatt, int status);
  public abstract void onDescriptorRead(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
  public abstract void onDescriptorWrite(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
      int status);
  public abstract void onCharacteristicWrite(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicRead(BleDevice bleDevice,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicChanged(BleDevice bleDevice,BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic);
  public abstract void onReadRemoteRssi(BleDevice bleDevice,BluetoothGatt gatt, int rssi, int status);
  public abstract void onReliableWriteCompleted(BleDevice bleDevice,BluetoothGatt gatt, int status);
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public abstract void onMtuChanged(BleDevice bleDevice,BluetoothGatt gatt, int mtu, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyRead(BleDevice bleDevice,BluetoothGatt gatt, int txPhy, int rxPhy, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyUpdate(BleDevice bleDevice,BluetoothGatt gatt, int txPhy, int rxPhy, int status);



  @Override
  public void onConnectionStateChangeBase(BleDevice bleDevice, BluetoothGatt gatt, int status,
      int newState) {
    runOnUiThread(()->onConnectionStateChange(bleDevice, gatt, status, newState));
  }

  @Override
  public void onServicesDiscoveredBase(BleDevice bleDevice, BluetoothGatt gatt, int status) {
    runOnUiThread(()->onServicesDiscovered(bleDevice, gatt, status));
  }

  @Override
  public void onDescriptorReadBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattDescriptor descriptor, int status) {
    runOnUiThread(()->onDescriptorRead(bleDevice, gatt, descriptor, status));
  }

  @Override
  public void onDescriptorWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattDescriptor descriptor, int status) {
    runOnUiThread(()->onDescriptorWrite(bleDevice, gatt, descriptor, status));
  }

  @Override
  public void onCharacteristicWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic, int status) {
    runOnUiThread(()->onCharacteristicWrite(bleDevice, gatt, characteristic, status));
  }

  @Override
  public void onCharacteristicReadBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic, int status) {
    runOnUiThread(()->onCharacteristicRead(bleDevice, gatt, characteristic, status));
  }

  @Override
  public void onCharacteristicChangedBase(BleDevice bleDevice, BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic) {
    runOnUiThread(()->onCharacteristicChanged(bleDevice, gatt, characteristic));
  }

  @Override
  public void onReadRemoteRssiBase(BleDevice bleDevice, BluetoothGatt gatt, int rssi, int status) {
    runOnUiThread(()->onReadRemoteRssi(bleDevice, gatt, rssi, status));
  }

  @Override
  public void onReliableWriteCompletedBase(BleDevice bleDevice, BluetoothGatt gatt, int status) {
    runOnUiThread(()->onReliableWriteCompleted(bleDevice, gatt, status));
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  public void onMtuChangedBase(BleDevice bleDevice, BluetoothGatt gatt, int mtu, int status) {
    runOnUiThread(()->onMtuChanged(bleDevice, gatt, mtu, status));
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onPhyReadBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
      int status) {
    runOnUiThread(()->onPhyRead(bleDevice, gatt, txPhy, rxPhy, status));
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onPhyUpdateBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
      int status) {
    runOnUiThread(()->onPhyUpdate(bleDevice, gatt, txPhy, rxPhy, status));
  }

  @Override
  public void onDeviceConnectingBase(BleDevice bleDevice) {

  }

  @Override
  public void onDeviceConnectedBase(BleDevice bleDevice) {

  }

  @Override
  public void onDeviceDisconnectingBase(BleDevice bleDevice) {

  }

  @Override
  public void onDeviceDisconnectedBase(BleDevice bleDevice) {

  }


}
