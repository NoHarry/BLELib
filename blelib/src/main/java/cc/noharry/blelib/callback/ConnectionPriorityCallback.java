package cc.noharry.blelib.callback;

import android.os.Build;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/07/05
 */
public interface ConnectionPriorityCallback extends TaskCallback {
   @RequiresApi(api = Build.VERSION_CODES.O)
   void onConnectionUpdated(BleDevice bleDevice, int interval, int latency, int timeout,
       int status);
}
