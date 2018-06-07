package cc.noharry.bledemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.util.Log;
import cc.noharry.bledemo.util.LogUtil;
import cc.noharry.bledemo.util.LogUtil.LogCallback;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class HomeViewmodel extends AndroidViewModel {
  private final SingleLiveEvent<Device> foundDevice=new SingleLiveEvent<>();
  private final SingleLiveEvent<Log> mLog=new SingleLiveEvent<>();
  private final SingleLiveEvent<Device> currentDevice=new SingleLiveEvent<>();
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private LogCallback mLogCallback;
  private BleScanCallback mBleScanCallback;
  private BleConnectCallback mBleConnectCallback;

  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()==Looper.getMainLooper()){
      runnable.run();
    }else {
      mHandler.post(runnable);
    }
  }

  public HomeViewmodel(@NonNull Application application) {
    super(application);
    initCallback();
  }

  private void initCallback() {
    mLogCallback = new LogCallback() {
      @Override
      public void onLog(Log log) {
        runOnUiThread(()->mLog.setValue(log));
      }
    };
    mBleScanCallback = new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {
        L.i("onScanStarted");
      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        foundDevice.setValue(device);
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {
        L.i("onScanCompleted");
      }
    };
    mBleConnectCallback = new BleConnectCallback() {
      @Override
      public void onDeviceConnecting(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTING);
        foundDevice.setValue(device);
      }

      @Override
      public void onDeviceConnected(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        foundDevice.setValue(device);
      }

      @Override
      public void onServicesDiscovered(BleDevice bleDevice, BluetoothGatt gatt, int status) {

      }

      @Override
      public void onDeviceDisconnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceDisconnected(BleDevice bleDevice, int status) {
        Device device=new Device(bleDevice);
        device.setState(Device.DISCONNECTED);
        foundDevice.setValue(device);
      }
    };
  }

  public void scan(){
    BleScanConfig config=new BleScanConfig.Builder().setScanTime(5000).build();
    BleAdmin
        .getINSTANCE(getApplication())
        .setLogEnable(true)
        .setLogStyle(BleAdmin.LOG_STYLE_DEFAULT)
        .scan(config, mBleScanCallback);
  }

  public void connect(Device device){
    BleAdmin
        .getINSTANCE(getApplication())
        .connect(device.getBleDevice()
        , false
        ,mBleConnectCallback);
  }

  public void disConnect(Device device){
    BleAdmin
        .getINSTANCE(getApplication())
        .disconnect(device.getBleDevice());
  }


  public void displayLog(){
    LogUtil.clearLog();
    LogUtil.readLog(mLogCallback);
  }

  public SingleLiveEvent<Log> getLog() {
    return mLog;
  }

  public SingleLiveEvent<Device> getFoundDevice() {
    return foundDevice;
  }
}
