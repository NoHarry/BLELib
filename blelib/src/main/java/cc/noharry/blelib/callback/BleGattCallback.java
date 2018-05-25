package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;

/**
 * @author NoHarry
 * @date 2018/05/21
 */

public abstract class BleGattCallback extends BluetoothGattCallback{
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

  public abstract void onConnectionStateChangeMain(BluetoothGatt gatt, int status, int newState);
  public abstract void onServicesDiscoveredMain(BluetoothGatt gatt, int status);
  public abstract void onDescriptorReadMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
  public abstract void onDescriptorWriteMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
      int status);
  public abstract void onCharacteristicWriteMain(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicReadMain(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status);
  public abstract void onCharacteristicChangedMain(BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic);
  public abstract void onReadRemoteRssiMain(BluetoothGatt gatt, int rssi, int status);
  public abstract void onReliableWriteCompletedMain(BluetoothGatt gatt, int status);
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public abstract void onMtuChangedMain(BluetoothGatt gatt, int mtu, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyReadMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status);
  @RequiresApi(api = Build.VERSION_CODES.O)
  public abstract void onPhyUpdateMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status);

  @Override
  public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    runOnUiThread(() -> onConnectionStateChangeMain(gatt, status, newState));
    /*runOnUiThread(new Runnable() {
      @Override
      public void run() {
        onConnectionStateChangeMain(gatt, status, newState);
      }
    });*/
  }

  @Override
  public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    runOnUiThread(() -> onServicesDiscoveredMain(gatt, status));
  }

  @Override
  public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    runOnUiThread(() -> onDescriptorReadMain(gatt, descriptor, status));
  }

  @Override
  public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
      int status) {
    runOnUiThread(() -> onDescriptorWriteMain(gatt, descriptor, status));
  }

  @Override
  public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status) {
    runOnUiThread(() -> onCharacteristicWriteMain(gatt, characteristic, status));
  }

  @Override
  public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
      int status) {
    runOnUiThread(() -> onCharacteristicReadMain(gatt, characteristic, status));
  }

  @Override
  public void onCharacteristicChanged(BluetoothGatt gatt,
      BluetoothGattCharacteristic characteristic) {
    runOnUiThread(() -> onCharacteristicChangedMain(gatt, characteristic));
  }

  @Override
  public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    runOnUiThread(() -> onReadRemoteRssiMain(gatt, rssi, status));
  }

  @Override
  public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    runOnUiThread(() -> onReliableWriteCompletedMain(gatt, status));
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    runOnUiThread(() -> onMtuChangedMain(gatt, mtu, status));
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
    runOnUiThread(() -> onPhyReadMain(gatt, txPhy, rxPhy, status));
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
    runOnUiThread(() -> onPhyUpdateMain(gatt, txPhy, rxPhy, status));
  }
}
