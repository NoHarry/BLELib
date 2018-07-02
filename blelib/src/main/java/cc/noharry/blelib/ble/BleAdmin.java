package cc.noharry.blelib.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.connect.BleConnectorProxy;
import cc.noharry.blelib.ble.connect.MultipleBleController;
import cc.noharry.blelib.ble.connect.Task;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanner;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author NoHarry
 * @date 2018/03/27
 */

public class BleAdmin {
  private static BleAdmin INSTANCE = null;
  private static Context mContext = null;
  private final BluetoothAdapter mBluetoothAdapter;
  private final Handler mHandler;
  private BTStateReceiver btStateReceiver = null;
  private final MultipleBleController mMultipleBleController;
  private final BleScanner mBleScanner;
  private final BleConnectorProxy mBleConnectorProxy;
  public static final int LOG_STYLE_DEFAULT=0;
  public static final int LOG_STYLE_LOGGER=1;
  private final BluetoothManager mBluetoothManager;


  private BleAdmin(Context context) {
    mContext = context.getApplicationContext();
    mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = mBluetoothManager.getAdapter();
    mHandler = new Handler(mContext.getMainLooper());
    btStateReceiver = new BTStateReceiver();
    mBleScanner = BleScanner.getINSTANCE(getContext());
    mMultipleBleController = MultipleBleController.getInstance(mContext);
    mBleConnectorProxy = BleConnectorProxy.getInstance(getContext());

  }

  public static BleAdmin getINSTANCE(Context context){
    if (INSTANCE == null){
      synchronized (BleAdmin.class){
        INSTANCE = new BleAdmin(context);
      }
    }
    return INSTANCE;
  }

  public static Context getContext() {
    return mContext;
  }

  public boolean isScanning(){
    return BleScanner.isScanning.get();
  }

  /**
   *
   * @param isEnableLog whther enable the debug log
   * @return BleAdmin
   */
  public BleAdmin setLogEnable(boolean isEnableLog){
    if (isEnableLog){
      L.isDebug=true;
    }else {
      L.isDebug=false;
    }
    return this;
  }

  public BleAdmin setLogStyle(int logStyle){
    L.logStyle=logStyle;
    return this;
  }

  /**
   * Return true if Bluetooth is currently enabled and ready for use.
   * @return true if the local adapter is turned on
   */
  public boolean isEnable(){
   return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
  }

  /**
   * Turn on the local Bluetooth adapter
   * @return true to indicate adapter startup has begun, or false on immediate error
   */
  public boolean openBT(){
    if (mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()){
      return mBluetoothAdapter.enable();
    }
    return false;
  }

  /**
   * Turn on the local Bluetooth adapter with a listener on {@link BluetoothAdapter#STATE_ON}
   * @param listener listen to the state of bluetooth adapter
   * @return true to indicate adapter startup has begun, or false on immediate error
   */
  public boolean openBT(OnBTOpenStateListener listener){
    btOpenStateListener=listener;
    registerBtStateReceiver(getContext());
    if (mBluetoothAdapter.isEnabled()){
      btOpenStateListener.onBTOpen();
      return true;
    }
    if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
      return mBluetoothAdapter.enable();
    }
    return false;
  }


  /**
   * Turn off the local Bluetooth adapter
    */
  public void closeBT() {
    if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
      mBluetoothAdapter.disable();
    }
  }

  public MultipleBleController getMultipleBleController(){
    return mMultipleBleController;
  }

  public BluetoothAdapter getBluetoothAdapter() {
    return mBluetoothAdapter;
  }

  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      , @NonNull BaseBleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, connectCallback);
  }


  @RequiresApi(api = VERSION_CODES.O)
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      ,@NonNull int preferredPhy, @NonNull BaseBleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, preferredPhy, connectCallback);
  }


  public void disconnect(@NonNull BleDevice bleDevice){
    mBleConnectorProxy.doDisconnect(bleDevice);
  }

  public void addTask(Task task){
    mBleConnectorProxy.enqueue(task);
  }

  public void scan(@NonNull BleScanConfig config,@NonNull BleScanCallback callback){
    if (isEnable()){
      if (!BleScanner.isScanning.get()){
        L.e("start scan");
        mBleScanner.scan(config,callback);
      }else {
        L.e("already start scan");
      }
    }else {
      L.e("Bluetooth not enabled");
    }

  }

  public void stopScan(){
    L.e("stop scan");
    mBleScanner.cancelScan();
  }

  public void removeAllTask(){
    mBleConnectorProxy.clear();
  }

  public int getConnectionState(BleDevice bleDevice){
    return mBleConnectorProxy.getConnectionState(bleDevice);
  }


  public void getConnectedDevice(){
    List<BleDevice> connectedDevice = mMultipleBleController.getConnectedDevice();
    L.i("connectedDevices:"+connectedDevice);

  }

  public void getConnectB(){
    List<BluetoothDevice> connectedDevices = mBluetoothManager
        .getConnectedDevices(BluetoothProfile.GATT_SERVER);
    mBleConnectorProxy.updateDevice();
    L.i("getConnectB:"+connectedDevices);
  }
  //检查已连接的蓝牙设备
  public void getConnectBt() {
    /*int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
    int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
    int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
    int flag = -1;
    if (a2dp == BluetoothProfile.STATE_CONNECTED) {
      flag = a2dp;
    } else if (headset == BluetoothProfile.STATE_CONNECTED) {
      flag = headset;
    } else if (health == BluetoothProfile.STATE_CONNECTED) {
      flag = health;
    }
    if (flag != -1) {
      mBluetoothAdapter.getProfileProxy(mContext, new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
          List<BluetoothDevice> mDevices = proxy.getConnectedDevices();
          if (mDevices != null && mDevices.size() > 0) {
            for (BluetoothDevice device : mDevices) {
              L.i(device.getName() + "," + device.getAddress());
            }
          } else {
            L.i("mDevices is null");
          }
        }
      }, flag);
    }*/

    Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
    try {//得到蓝牙状态的方法
      Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
      //打开权限
      method.setAccessible(true);
      int state = (int) method.invoke(mBluetoothAdapter, (Object[]) null);
      L.i("state:"+state);
      if(state == BluetoothAdapter.STATE_CONNECTED){

        L.i("BluetoothAdapter.STATE_CONNECTED");

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        L.i("devices:"+devices.size());

        for(BluetoothDevice device : devices){

          Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
          method.setAccessible(true);
          boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);

          if(isConnected){
            L.i("connected:"+device.getAddress());
          }

        }

      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void registerBtStateReceiver(Context context) {
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    context.registerReceiver(btStateReceiver, filter);
  }

  private void unRegisterBtStateReceiver(Context context) {
    try {
      context.unregisterReceiver(btStateReceiver);
    } catch (Exception e) {
    } catch (Throwable e) {
    }

  }

  private OnBTOpenStateListener btOpenStateListener = null;

  public interface OnBTOpenStateListener {
    void onBTOpen();
  }

  private class BTStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent intent) {
      String action = intent.getAction();
      L.i("action=" + action);
      if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        int state = intent
            .getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
        L.i("state=" + state);
        switch (state) {
          case BluetoothAdapter.STATE_TURNING_ON:
            L.i("ACTION_STATE_CHANGED:  STATE_TURNING_ON");
            break;
          case BluetoothAdapter.STATE_ON:
            L.i("ACTION_STATE_CHANGED:  STATE_ON");
            if (null != btOpenStateListener){
              btOpenStateListener.onBTOpen();
            }
            unRegisterBtStateReceiver(getContext());
            break;
          default:
        }
      }
    }
  }



}
