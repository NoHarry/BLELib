package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;

/**
 * @author NoHarry
 * @date 2018/05/29
 */

public interface WriteCallback extends TaskCallback{
  void onDataSent(BleDevice bleDevice,Data data,int totalPackSize,int remainPackSize);
}
