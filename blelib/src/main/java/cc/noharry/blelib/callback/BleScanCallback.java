package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public abstract class BleScanCallback {
  public static final int SCAN_FAIL_ADAPTER_NOT_ENABLE=1001;
  public static final int SCAN_FAIL_SCAN_ALREADLY_STARTED=1002;
  public static final int SCAN_FAIL_SCAN_WRONG_FILTER=1003;


  public abstract void onScanStarted(boolean isStartSuccess);
  public abstract void onFoundDevice(BleDevice bleDevice);
  public abstract void onScanCompleted(List<BleDevice> deviceList);
  public abstract void onScanFail(int errorCode,String msg);

}
