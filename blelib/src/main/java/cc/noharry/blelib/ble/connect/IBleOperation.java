package cc.noharry.blelib.ble.connect;

import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public interface IBleOperation {
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,BleConnectCallback callback);
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,int preferredPhy,BleConnectCallback callback);
  void doDisconnect(BleDevice bleDevice);
  void doTask(BleDevice bleDevice,Task task);
}
