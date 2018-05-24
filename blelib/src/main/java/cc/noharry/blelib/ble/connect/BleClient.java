package cc.noharry.blelib.ble.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.BLEAdmin;
import cc.noharry.blelib.callback.BleGattCallback;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/18
 */

public class BleClient implements BleFactory{
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private BleDevice mBleDevice;
  private BluetoothGatt gatt;

  public BleClient(BleDevice bleDevice) {
    mBleDevice = bleDevice;
  }

  @Override
  public BleConnector getConnector() {
    return BleConnector.getInstance(this);
  }

  @SuppressLint("NewApi")
  public synchronized BluetoothGatt connect(boolean isAutoConnect,BleGattCallback callback){
      return connect(isAutoConnect,BluetoothDevice.PHY_LE_1M_MASK,callback);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public synchronized BluetoothGatt connect(boolean isAutoConnect,int preferredPhy,BleGattCallback callback){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BLEAdmin.getContext(),
          isAutoConnect, callback, BluetoothDevice.TRANSPORT_LE, preferredPhy, mHandler);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BLEAdmin.getContext(),
          isAutoConnect, callback, BluetoothDevice.TRANSPORT_LE);
    } else {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BLEAdmin.getContext(),
          isAutoConnect, callback);
    }
    return gatt;
  }

}
