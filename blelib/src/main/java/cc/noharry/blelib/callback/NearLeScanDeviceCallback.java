package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * @author NoHarry
 * @date 2018/04/09
 */

public class NearLeScanDeviceCallback implements BluetoothAdapter.LeScanCallback{


  @Override
  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

  }
}
