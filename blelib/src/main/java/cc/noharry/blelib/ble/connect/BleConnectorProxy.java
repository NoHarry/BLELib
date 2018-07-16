package cc.noharry.blelib.ble.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.exception.GattError;
import cc.noharry.blelib.util.L;
import cc.noharry.blelib.util.ThreadPoolProxyFactory;
import java.util.List;
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
  /**
   * Task queue
   */
  private BlockingDeque<Task> mBlockingDeque;
  /**
   * Connection queue
   */
  private BlockingDeque<ConnectionRequest> mConnectionDeque;
  private AtomicBoolean isOperating;
  private AtomicBoolean isConnecting;
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private BluetoothManager mBluetoothManager;
  private BleConnectCallback mDefaultCallback;

  private BleConnectorProxy(Context context){
    mContext=context;
    mMultipleBleController = MultipleBleController.getInstance(mContext);
    mBluetoothManager = (BluetoothManager) mContext
        .getSystemService(Context.BLUETOOTH_SERVICE);
    isOperating=new AtomicBoolean(false);
    isConnecting=new AtomicBoolean(false);
    initQueue();
    initCallback();

  }

  private void initCallback() {
    mDefaultCallback = new BleConnectCallback() {
      @Override
      public void onDeviceConnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceConnected(BleDevice bleDevice) {

      }

      @Override
      public void onServicesDiscovered(BleDevice bleDevice, BluetoothGatt gatt, int status) {

      }

      @Override
      public void onDeviceDisconnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceDisconnected(BleDevice bleDevice, int status) {

      }
    };
  }

  private void initQueue() {
    mBlockingDeque = new LinkedBlockingDeque();
    mConnectionDeque=new LinkedBlockingDeque<>();
    ThreadPoolProxyFactory.getTaskThreadPoolProxy().submit(()->{
      while (true){
        try {
          if (!isOperating.get()){
            Task task = mBlockingDeque.take();
            doTask(task);
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });


    ThreadPoolProxyFactory.getConnectionThreadPoolProxy().submit(()->{
      while (true){
        try {
          if (!isConnecting.get()){
            ConnectionRequest request = mConnectionDeque.take();
            doConnection(request);
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @SuppressLint("NewApi")
  private void doConnection(ConnectionRequest request) {
    switch (request.getType()){
      case ConnectionRequest.CONNECTION_NORMAL:
        isConnecting.set(true);
        runOnUiThread(() -> getBleClient(request.getBleDevice())
            .doConnect(request.getBleDevice(),request.isAutoConnect(),request.getBaseBleConnectCallback()));
        break;
      case ConnectionRequest.CONNECTION_NORMAL_WITH_TIMEOUT:
        isConnecting.set(true);
        runOnUiThread(() -> getBleClient(request.getBleDevice())
            .doConnect(request.getBleDevice(),request.isAutoConnect()
                ,request.getBaseBleConnectCallback(),request.getTimeOut()));
        break;
      case ConnectionRequest.CONNECTION_O:
        isConnecting.set(true);
        runOnUiThread(() -> getBleClient(request.getBleDevice())
            .doConnect(request.getBleDevice(),request.isAutoConnect()
                ,request.getPreferredPhy(),request.getBaseBleConnectCallback()));
        break;
      case ConnectionRequest.CONNECTION_O_WITH_TIMEOUT:
        isConnecting.set(true);
        runOnUiThread(() -> getBleClient(request.getBleDevice())
            .doConnect(request.getBleDevice(),request.isAutoConnect()
                ,request.getPreferredPhy(),request.getBaseBleConnectCallback(),request.getTimeOut()));
        break;
        default:
    }
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
    ConnectionRequest request=new ConnectionRequest(bleDevice,isAutoConnect,callback);
    try {
      mConnectionDeque.put(request);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BaseBleConnectCallback callback,
      long timeOut) {
    ConnectionRequest request=new ConnectionRequest(bleDevice,isAutoConnect,callback,timeOut);
    try {
      mConnectionDeque.put(request);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback) {
    ConnectionRequest request=new ConnectionRequest(bleDevice,isAutoConnect,callback,preferredPhy);
    try {
      mConnectionDeque.put(request);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BaseBleConnectCallback callback, long timeOut) {
    ConnectionRequest request=new ConnectionRequest(bleDevice,isAutoConnect,callback,preferredPhy,timeOut);
    try {
      mConnectionDeque.put(request);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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


  public void clear(){
    mBlockingDeque.clear();
  }

  public int getConnectionState(BleDevice bleDevice){
    return getBleClient(bleDevice).getCurrentConnectionState();
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

  public void updateDevice(){
    List<BluetoothDevice> connectedDevices = mBluetoothManager
        .getConnectedDevices(BluetoothProfile.GATT_SERVER);
    for (BluetoothDevice device:connectedDevices){
      BleDevice bleDevice=new BleDevice(device,new byte[]{},0,System.currentTimeMillis());
      BleClient bleClient = getBleClient(bleDevice);
      bleClient.connect(false,mDefaultCallback);
    }
  }

  public void enqueue(Task task){
    L.i("enqueue task:"+" TYPE:"+task.getType()+" Device:"+task.getBleDevice().getMac());
    try {
      mBlockingDeque.put(task);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void taskNotify(int state){
    L.i("Task Finished:"+state+" message:"+ GattError.parse(state));
    isOperating.set(false);
  }

  public void connectionNotify(int state){
    L.i("connectionNotify:"+state+" message:"+ GattError.parseConnectionError(state));
    isConnecting.set(false);
  }
}
