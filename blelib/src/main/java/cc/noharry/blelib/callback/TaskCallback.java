package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public interface TaskCallback {
  void onCompleted(BleDevice bleDevice,int statuCode);
  void onFail(BleDevice bleDevice,int statuCode);
}
