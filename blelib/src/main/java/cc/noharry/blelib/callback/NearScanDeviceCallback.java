package cc.noharry.blelib.callback;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import cc.noharry.blelib.ble.BleScanConfig;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/04/09
 */

@TargetApi(VERSION_CODES.LOLLIPOP)
public class NearScanDeviceCallback extends ScanCallback {
  private BleScanConfig mBleScanConfig;
  private NearScanCallback mNearScanCallback;
  private Context mContext;




  public NearScanDeviceCallback(Context context,BleScanConfig bleScanConfig,BleScanCallback callback) {
    mBleScanConfig = bleScanConfig;
    mContext=context;
    mNearScanCallback = new NearScanCallback(mContext,bleScanConfig,callback,this);

  }

  @Override
  public void onScanResult(int callbackType, ScanResult result) {
    BleDevice bleDevice=new BleDevice(result.getDevice(),
        result.getScanRecord().getBytes(),result.getRssi(),result.getTimestampNanos());
    mNearScanCallback.onFoundDevice(bleDevice);
//    L.i("onScanResult "+"callbackType:"+callbackType+" ScanResult:"+result);
  }

  @Override
  public void onBatchScanResults(List<ScanResult> results) {
    L.i("onBatchScanResults"+" results:"+results);
  }

  @Override
  public void onScanFailed(int errorCode) {
    L.i("onScanFailed:"+errorCode);
  }

  public void onScanStart(boolean isScanStarted){
    mNearScanCallback.onScanStarted(isScanStarted);
  }

  public void onScanCancel(){

  }

}
