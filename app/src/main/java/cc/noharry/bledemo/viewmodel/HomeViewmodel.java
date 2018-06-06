package cc.noharry.bledemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.util.Log;
import cc.noharry.bledemo.util.LogUtil;
import cc.noharry.bledemo.util.LogUtil.logCallback;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.scan.BleScanConfig;
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
  private Handler mHandler=new Handler(Looper.getMainLooper());

  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()==Looper.getMainLooper()){
      runnable.run();
    }else {
      mHandler.post(runnable);
    }
  }

  public HomeViewmodel(@NonNull Application application) {
    super(application);
  }

  public void scan(){
    BleScanConfig config=new BleScanConfig.Builder().setScanTime(5000).build();
    BleAdmin.getINSTANCE(getApplication()).setLogEnable(false).scan(config, new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {

      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        foundDevice.setValue(device);
//        L.i("onFoundDevice"+bleDevice);
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {

      }
    });
  }

  public void displayLog(){
    LogUtil.clearLog();
    LogUtil.readLog(new logCallback() {
      @Override
      public void onLog(Log log) {
        runOnUiThread(()->mLog.setValue(log));
      }
    });
  }

  public SingleLiveEvent<Log> getLog() {
    return mLog;
  }

  public SingleLiveEvent<Device> getFoundDevice() {
    return foundDevice;
  }
}
