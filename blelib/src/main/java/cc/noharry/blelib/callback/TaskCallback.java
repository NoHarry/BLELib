package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public interface TaskCallback {
  void onOperationCompleted(BleDevice bleDevice);
  void onFail(BleDevice bleDevice,int statuCode,String message);
}
