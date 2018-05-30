package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;

/**
 * @author NoHarry
 * @date 2018/05/30
 */

public interface DataChangeCallback extends TaskCallback{
  void onDataChange(BleDevice bleDevice,Data data);
}
