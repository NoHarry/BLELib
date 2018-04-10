package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public abstract class BleScanCallback {
  public abstract void onScanStarted(boolean isStartSuccess);
  public abstract void onFoundDevice(BleDevice bleDevice);
  public abstract void onScanCompleted(List<BleDevice> deviceList);

}
