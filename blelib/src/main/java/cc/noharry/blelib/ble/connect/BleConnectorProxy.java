package cc.noharry.blelib.ble.connect;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.MultipleBleController;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.exception.GattError;
import cc.noharry.blelib.util.L;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/05/21
 */

public class BleConnectorProxy implements IBleOperation{
  private static BleConnectorProxy instance=null;
  private Context mContext;
  private final MultipleBleController mMultipleBleController;
  private BlockingDeque<Task> mBlockingDeque;
  private AtomicBoolean isOperating;
  private Handler mHandler=new Handler(Looper.getMainLooper());

  private BleConnectorProxy(Context context){
    mContext=context;
    mMultipleBleController = MultipleBleController.getInstance(mContext);
    isOperating=new AtomicBoolean(false);
    initQueue();
  }

  private void initQueue() {
    mBlockingDeque = new LinkedBlockingDeque();
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          try {
            if (!isOperating.get()){
              Task task = mBlockingDeque.take();
              doTask(task);
            }

          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          /*if (!mBlockingDeque.isEmpty()&&!isOperating.get()){
            doTask(mBlockingDeque.poll());
          }*/

        }
      }
    }).start();

  }

  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()==Looper.getMainLooper()){
      runnable.run();
    }else {
      mHandler.post(runnable);
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
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BaseBleConnectCallback callback) {
    runOnUiThread(() -> getBleClient(bleDevice).connect(isAutoConnect,callback));

  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback) {
    runOnUiThread(() -> getBleClient(bleDevice).connect(isAutoConnect,preferredPhy,callback));

  }

  @Override
  public void doDisconnect(BleDevice bleDevice) {
    runOnUiThread(() -> getBleClient(bleDevice).disconnect());

  }

  @Override
  public void doTask(Task task) {
    isOperating.set(true);
    runOnUiThread(()->getBleClient(task.getBleDevice()).doTask(task));
  }

  private BleClient getBleClient(BleDevice bleDevice) {
    BleClient bleClient = null;
    if (MultipleBleController.getInstance(mContext).getClientMap().containsKey(bleDevice.getKey())) {
      bleClient = MultipleBleController.getInstance(mContext).getClientMap().get(bleDevice.getKey());
    } else {
      bleClient = new BleClient(bleDevice);
    }
    return bleClient;
  }

  public void enqueue(Task task){
    L.e("enqueue task:"+task);
    try {
      mBlockingDeque.put(task);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void taskNotify(int state){
    L.e("taskNotify:"+state+" message:"+ GattError.parse(state));
    isOperating.set(false);
  }
}
