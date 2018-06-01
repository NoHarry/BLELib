package cc.noharry.bledemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import cc.noharry.bledemo.SingleLiveEvent;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.util.L;
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

  public HomeViewmodel(@NonNull Application application) {
    super(application);
  }

  public void scan(){
    BleScanConfig config=new BleScanConfig.Builder().setScanTime(5000).build();
    BleAdmin.getINSTANCE(getApplication()).scan(config, new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {

      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        foundDevice.setValue(device);
        L.i("onFoundDevice"+bleDevice);
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {

      }
    });
  }

  public SingleLiveEvent<Device> getFoundDevice() {
    return foundDevice;
  }
}
