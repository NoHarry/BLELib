package cc.noharry.blelib.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.connect.BleConnectorProxy;
import cc.noharry.blelib.ble.connect.Task;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanner;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;

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

  private BleAdmin(Context context) {
    mContext = context.getApplicationContext();
    BluetoothManager bluetoothManager= (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();
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
      , @NonNull BleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, connectCallback);
  }


  @RequiresApi(api = VERSION_CODES.O)
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      ,@NonNull int preferredPhy, @NonNull BleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, preferredPhy, connectCallback);
  }


  public void disconnect(@NonNull BleDevice bleDevice){
    mBleConnectorProxy.doDisconnect(bleDevice);
  }

  public void addTask(BleDevice bleDevice,Task task){
    mBleConnectorProxy.doTask(bleDevice,task);
  }

  public void scan(@NonNull BleScanConfig config,@NonNull BleScanCallback callback){
    if (!BleScanner.isScanning.get()){
      L.e("start scan");
      mBleScanner.scan(config,callback);
    }else {
      L.e("already start scan");
    }
  }

  public void stopScan(){
    L.e("stop scan");
    mBleScanner.cancelScan();
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
