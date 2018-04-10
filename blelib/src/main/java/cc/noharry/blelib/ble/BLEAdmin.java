package cc.noharry.blelib.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.util.L;

/**
 * @author NoHarry
 * @date 2018/03/27
 */

public class BLEAdmin {
  private static BLEAdmin INSTANCE = null;
  private Context mContext = null;
  private final BluetoothAdapter mBluetoothAdapter;
  private final Handler mHandler;
  private BTStateReceiver btStateReceiver = null;

  private BLEAdmin(Context context) {
    mContext = context.getApplicationContext();
    BluetoothManager bluetoothManager= (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();
    mHandler = new Handler(mContext.getMainLooper());
    btStateReceiver = new BTStateReceiver();
  }

  public static BLEAdmin getINSTANCE(Context context){
    if (INSTANCE == null){
      synchronized (BLEAdmin.class){
        INSTANCE = new BLEAdmin(context);
      }
    }
    return INSTANCE;
  }


  /**
   *
   * @param isEnableLog whther enable the debug log
   * @return BLEAdmin
   */
  public BLEAdmin setLogEnable(boolean isEnableLog){
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
    registerBtStateReceiver(mContext);
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

  public void scan(BleScanConfig config,BleScanCallback callback){
      L.e("开始扫描");
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
      ParcelUuid uuid=new ParcelUuid(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
      ScanFilter filter=new ScanFilter.Builder().setServiceUuid(uuid).build();
      List<ScanFilter> list=new ArrayList<>();
      list.add(filter);
      mBluetoothAdapter.getBluetoothLeScanner().startScan(list,new ScanSettings.Builder().build(),mCallBack);
    }else {

    }*/
    if (!BleScanner.isScanning.get()){
      BleScanner.getINSTANCE(mContext).scan(mBluetoothAdapter,config,callback);
    }

  }

  @TargetApi(VERSION_CODES.LOLLIPOP)
  public void stopScan(){
    L.e("停止扫描");
    BleScanner.getINSTANCE(mContext).stopScan(mBluetoothAdapter);
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
            unRegisterBtStateReceiver(mContext);
            break;
          default:
        }
      }
    }
  }



}
