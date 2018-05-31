package cc.noharry.blelib.ble.connect;

import android.content.Context;
import cc.noharry.blelib.data.BleDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author NoHarry
 * @date 2018/05/25
 */

 public class MultipleBleController {
  private Context mContext;
  private static MultipleBleController instance=null;
  private ConcurrentMap<String,BleClient> clientMap=new ConcurrentHashMap<>();

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

  protected ConcurrentMap<String, BleClient> getClientMap() {
    return clientMap;
  }

  public List<BleDevice> getConnectedDevice(){
    List<BleDevice> result=new ArrayList<>();
    for (String s:clientMap.keySet()){
      BleClient bleClient = clientMap.get(s);
      if (bleClient.getIsConnected().get()){
        result.add(bleClient.getBleDevice());
      }
    }
    return result;
  }
}
