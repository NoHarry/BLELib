package cc.noharry.blelib.ble;

import android.content.Context;
import cc.noharry.blelib.ble.connect.BleClient;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

 public class MultipleBleController {
  private Context mContext;
  private static MultipleBleController instance=null;
  private ConcurrentHashMap<String,BleClient> clientMap=new ConcurrentHashMap<>();

  private MultipleBleController(Context context) {
    mContext = context;
  }

  public static MultipleBleController getInstance(Context context){
    if (instance==null){
      synchronized (MultipleBleController.class){
        if (instance==null){
          instance=new MultipleBleController(context);
        }
      }
    }
    return instance;
  }

  public ConcurrentHashMap<String, BleClient> getClientMap() {
    return clientMap;
  }
}
