package cc.noharry.blelib.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import cc.noharry.blelib.ble.connect.MultipleBleController;
import cc.noharry.blelib.ble.connect.Task;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanner;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.List;

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
  /**
   * System log style
   */
  public static final int LOG_STYLE_DEFAULT=0;
  /** like this style
   * ┌─────────────
   * |    log
   * |┄┄┄┄┄┄┄┄┄┄┄┄┄
   * |
   * |┄┄┄┄┄┄┄┄┄┄┄┄┄
   * |
   * └──────────────
   */
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

  /**
   * Get scan status
   * @return
   */
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
   * Set different log print formats
   * @param logStyle one of {@link BleAdmin#LOG_STYLE_DEFAULT},{@link BleAdmin#LOG_STYLE_LOGGER}
   * @return
   */
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

  /*public MultipleBleController getMultipleBleController(){
    return mMultipleBleController;
  }*/

  public BluetoothAdapter getBluetoothAdapter() {
    return mBluetoothAdapter;
  }

  /**
   * Connect to GATT Server hosted by this device
   * @param bleDevice Target device
   * @param isAutoConnect Whether to directly connect to the remote device (false)
   *                      or to automatically connect as soon as the remote
   *                      device becomes available (true).
   * @param connectCallback GATT callback handler that will receive asynchronous callbacks,
   *                        use {@link BaseBleConnectCallback} to fully handle the callback,
   *                        or {@link cc.noharry.blelib.callback.BleConnectCallback} in a simple way.
   */
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      , @NonNull BaseBleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, connectCallback);
  }

  /**
   * Connect to GATT Server hosted by this device
   * @param bleDevice Target device
   * @param isAutoConnect Whether to directly connect to the remote device (false)
   *                      or to automatically connect as soon as the remote
   *                      device becomes available (true).
   * @param connectCallback GATT callback handler that will receive asynchronous callbacks,
   *                        use {@link BaseBleConnectCallback} to fully handle the callback,
   *                        or {@link cc.noharry.blelib.callback.BleConnectCallback} in a simple way.
   * @param timeOut Connection timeout with milliseconds
   */
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      , @NonNull BaseBleConnectCallback connectCallback,@NonNull long timeOut){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, connectCallback,timeOut);
  }


  /**
   * Connect to GATT Server hosted by this device
   * @param bleDevice Target device
   * @param isAutoConnect Whether to directly connect to the remote device (false)
   *                      or to automatically connect as soon as the remote
   *                      device becomes available (true).
   * @param preferredPhy preferred PHY for connections to remote LE device. Bitwise OR of any of
   *                      {@link BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK},
   *                      an d{@link BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect
   *                      if {@code autoConnect} is set to true.
   * @param connectCallback GATT callback handler that will receive asynchronous callbacks,
   *                        use {@link BaseBleConnectCallback} to fully handle the callback,
   *                        or {@link cc.noharry.blelib.callback.BleConnectCallback} in a simple way.
   */
  @RequiresApi(api = VERSION_CODES.O)
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      ,@NonNull int preferredPhy, @NonNull BaseBleConnectCallback connectCallback){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, preferredPhy, connectCallback);
  }

  /**
   * Connect to GATT Server hosted by this device
   * @param bleDevice Target device
   * @param isAutoConnect Whether to directly connect to the remote device (false)
   *                      or to automatically connect as soon as the remote
   *                      device becomes available (true).
   * @param preferredPhy preferred PHY for connections to remote LE device. Bitwise OR of any of
   *                      {@link BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK},
   *                      an d{@link BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect
   *                      if {@code autoConnect} is set to true.
   * @param connectCallback GATT callback handler that will receive asynchronous callbacks,
   *                        use {@link BaseBleConnectCallback} to fully handle the callback,
   *                        or {@link cc.noharry.blelib.callback.BleConnectCallback} in a simple way.
   * @param timeOut Connection timeout with milliseconds
   */
  @RequiresApi(api = VERSION_CODES.O)
  public void connect(@NonNull BleDevice bleDevice,@NonNull boolean isAutoConnect
      ,@NonNull int preferredPhy, @NonNull BaseBleConnectCallback connectCallback,long timeOut){
    mBleConnectorProxy.doConnect(bleDevice, isAutoConnect, preferredPhy, connectCallback,timeOut);
  }

  /**
   * Disconnect from this device
   * @param bleDevice Target device
   */
  public void disconnect(@NonNull BleDevice bleDevice){
    mBleConnectorProxy.doDisconnect(bleDevice);
  }

  /**
   * Disconnect all connected devices
   */
  public void disconnectAllDevices(){
    mMultipleBleController.disConnectAllDevice();
  }

  /**
   * Enqueue task
   * @param task
   */
  public void addTask(Task task){
    mBleConnectorProxy.enqueue(task);
  }

  /**
   * Scanning peripherals
   * @param config scan rules
   * @param callback callback of scan result
   */
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

  /**
   * Stop scanning
   */
  public void stopScan(){
    L.e("stop scan");
    mBleScanner.cancelScan();
  }

  /**
   * Remove all tasks from the task queue
   */
  public void removeAllTask(){
    mBleConnectorProxy.clear();
  }

  /**
   * Get the current connection status of the device
   * @param bleDevice target device
   * @return one of
   *  {@link android.bluetooth.BluetoothProfile#STATE_CONNECTED}
   * ,{@link android.bluetooth.BluetoothProfile#STATE_CONNECTING}
   * ,{@link android.bluetooth.BluetoothProfile#STATE_DISCONNECTING}
   * ,{@link android.bluetooth.BluetoothProfile#STATE_DISCONNECTED}
   */
  public int getConnectionState(BleDevice bleDevice){
    return mBleConnectorProxy.getConnectionState(bleDevice);
  }

  /**
   * Get connected devices
   * @return list of connected devices
   */
  public List<BleDevice> getConnectedDevice(){
    List<BleDevice> connectedDevice = mMultipleBleController.getConnectedDevice();
    L.i("connectedDevices:"+connectedDevice);
    return connectedDevice;
  }

  /*public void getConnectB(){
    List<BluetoothDevice> connectedDevices = mBluetoothManager
        .getConnectedDevices(BluetoothProfile.GATT_SERVER);
    mBleConnectorProxy.updateDevice();
    L.i("getConnectB:"+connectedDevices);
  }*/


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
