package cc.noharry.blelib.ble.connect;

import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Looper;
import cc.noharry.blelib.callback.BleGattCallback;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/18
 */

public class BleClient implements BleFactory{
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private BleDevice mBleDevice;

  private BleClient(BleDevice bleDevice) {
    mBleDevice = bleDevice;
  }


  @Override
  public BleClient newInstance(BleDevice bleDevice) {
    return new BleClient(bleDevice);
  }

  @Override
  public BleConnector getConnector() {
    return BleConnector.getInstance(this);
  }

  public synchronized BluetoothGatt connect(boolean isAutoConnect,BleGattCallback callback){
      return null;
  }
}
