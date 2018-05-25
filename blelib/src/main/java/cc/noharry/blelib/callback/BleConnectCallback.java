package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

public interface BleConnectCallback {
  void onDeviceConnecting(BleDevice bleDevice);
  void onDeviceConnected(BleDevice bleDevice);
  void onDeviceDisconnecting(BleDevice bleDevice);
  void onDeviceDisconnected(BleDevice bleDevice);
  void onServicesDiscovered(BleDevice bleDevice);
  void onDataSent(BleDevice bleDevice,byte[] data);
  void onDataRecived(BleDevice bleDevice,byte[] data);
}
