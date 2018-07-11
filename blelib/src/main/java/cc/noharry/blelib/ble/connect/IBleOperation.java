package cc.noharry.blelib.ble.connect;

import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public interface IBleOperation {
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,BaseBleConnectCallback callback);
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,BaseBleConnectCallback callback,long timeOut);
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,int preferredPhy,BaseBleConnectCallback callback);
  void doConnect(BleDevice bleDevice,boolean isAutoConnect,int preferredPhy,BaseBleConnectCallback callback,long timeOut);
  void doDisconnect(BleDevice bleDevice);
  void doTask(Task task);
}
