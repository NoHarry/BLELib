package cc.noharry.blelib.callback;

import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/06/29
 */
public interface MtuCallback extends TaskCallback {
  void onMtuChanged(BleDevice bleDevice,int mtu);

}
