package cc.noharry.blelib.ble.connect;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.ConditionVariable;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.MultipleBleController;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author NoHarry
 * @date 2018/05/21
 */

public class BleConnectorProxy implements IBleOperation{
  private static BleConnectorProxy instance=null;
  private Context mContext;
  private final MultipleBleController mMultipleBleController;
  private BlockingDeque<Map<BleDevice,Task>> mBlockingDeque;
  private final ConditionVariable mLock;

  private BleConnectorProxy(Context context){
    mContext=context;
    mMultipleBleController = MultipleBleController.getInstance(mContext);
    mLock = new ConditionVariable(true);
    initQueue();
  }

  private void initQueue() {
    mBlockingDeque = new LinkedBlockingDeque();
    while (!mBlockingDeque.isEmpty()){

    }
  }

  public static BleConnectorProxy getInstance(Context context){
    if (instance==null){
      synchronized (BleConnectorProxy.class){
        if (instance==null){
          instance=new BleConnectorProxy(context);
        }
      }
    }
    return instance;
  }

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BleConnectCallback callback) {
    BleClient bleClient=null;
    if (mMultipleBleController.getClientMap().containsKey(bleDevice.getKey())){
      bleClient=mMultipleBleController.getClientMap().get(bleDevice.getKey());
    }else {
      bleClient=new BleClient(bleDevice);
    }
    bleClient.connect(isAutoConnect,callback);
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BleConnectCallback callback) {
    BleClient bleClient=null;
    if (mMultipleBleController.getClientMap().containsKey(bleDevice.getKey())){
      bleClient=mMultipleBleController.getClientMap().get(bleDevice.getKey());
    }else {
      bleClient=new BleClient(bleDevice);
    }
    bleClient.connect(isAutoConnect,preferredPhy,callback);
  }

  @Override
  public void doDisconnect(BleDevice bleDevice) {
    BleClient bleClient=null;
    if (mMultipleBleController.getClientMap().containsKey(bleDevice.getKey())){
      bleClient=mMultipleBleController.getClientMap().get(bleDevice.getKey());
    }else {
      bleClient=new BleClient(bleDevice);
    }
    bleClient.disconnect();
  }

  @Override
  public void doTask(BleDevice bleDevice, Task task) {
    BleClient bleClient=null;
    if (mMultipleBleController.getClientMap().containsKey(bleDevice.getKey())){
      bleClient=mMultipleBleController.getClientMap().get(bleDevice.getKey());
    }else {
      bleClient=new BleClient(bleDevice);
    }
    bleClient.doTask(bleDevice, task);
  }



  public void taskNotity(int state){
    L.e("taskNotity:"+state);
  }
}
