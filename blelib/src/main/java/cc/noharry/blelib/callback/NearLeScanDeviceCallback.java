package cc.noharry.blelib.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import cc.noharry.blelib.ble.BleScanConfig;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/04/09
 */

public class NearLeScanDeviceCallback implements BluetoothAdapter.LeScanCallback{
  private BleScanConfig mBleScanConfig;
  private NearScanCallback mNearScanCallback;
  private Context mContext;

  public NearLeScanDeviceCallback(BleScanConfig bleScanConfig,
      BleScanCallback callback, Context context) {
    mBleScanConfig = bleScanConfig;
    mNearScanCallback = new NearScanCallback(mContext,mBleScanConfig,callback,this);
    mContext = context;
  }

  @Override
  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    BleDevice bleDevice=new BleDevice(device,scanRecord,rssi,System.currentTimeMillis());
    mNearScanCallback.onFoundDevice(bleDevice);
  }

  public void onScanStart(boolean isScanStarted){
    mNearScanCallback.onScanStarted(isScanStarted);
  }

  public void onScanCancel(){
    mNearScanCallback.onScanCancel();
  }
}
