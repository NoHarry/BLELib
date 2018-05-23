package cc.noharry.blelib.ble.connect;

import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/05/21
 */

public interface BleFactory {

  BleClient newInstance(BleDevice bleDevice);
  BleConnector getConnector();

}
