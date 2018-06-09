package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public interface ReadCallback extends TaskCallback{
  void onDataRecived(BleDevice bleDevice,Data data);

}
