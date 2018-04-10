package cc.noharry.blelib.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.ParcelUuid;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.callback.NearScanDeviceCallback;
import cc.noharry.blelib.util.L;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public class BleScanner {
  private static BleScanner INSTANCE=null;
  private Context mContext=null;
  private NearScanDeviceCallback mScanDeviceCallback;
  public static AtomicBoolean isScanning=new AtomicBoolean(false);

  private BleScanner(Context context) {
    mContext = context;
  }

  public static BleScanner getINSTANCE(Context context){
    if (INSTANCE==null){
      synchronized (BleScanner.class){
        if (INSTANCE==null){
          INSTANCE=new BleScanner(context);
        }
      }
    }
    return INSTANCE;
  }

  public void scan(BluetoothAdapter adapter,BleScanConfig config,BleScanCallback callback){
    /*try {
      MethodUtils.checkScanConfig(config);
    } catch (MacFormatException e) {
      e.printStackTrace();
      return;
    }*/
    mScanDeviceCallback = new NearScanDeviceCallback(mContext,config,callback);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
      try {
        handleScanNew(adapter, config);
      }catch (IllegalArgumentException e){
        L.e("isScanning:"+isScanning.get());
        e.printStackTrace();
      }
    }else {

    }
  }

  public void stopScan(BluetoothAdapter adapter){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
     adapter.getBluetoothLeScanner().stopScan(mScanDeviceCallback);
     isScanning.set(false);
    }else {

    }
  }



  @TargetApi(VERSION_CODES.LOLLIPOP)
  private void handleScanNew(BluetoothAdapter adapter, BleScanConfig config) {
    List<ScanFilter> list=new ArrayList<>();
    UUID[] uuids = config.getUUIDS();
    if (uuids!=null){
      for (UUID u:uuids){
        ParcelUuid uuid=new ParcelUuid(u);
        ScanFilter filter=new ScanFilter.Builder().setServiceUuid(uuid).build();
        list.add(filter);
      }
    }
    String[] deviceMac = config.getDeviceMac();
    if (deviceMac!=null){
      for (String s:deviceMac){
        ScanFilter filter=new ScanFilter.Builder().setDeviceAddress(s).build();
        list.add(filter);
      }
    }

    ScanFilter filter=new ScanFilter.Builder().setDeviceName("LL20623").build();
//    ScanFilter filter1=new ScanFilter.Builder().setDeviceName("LL21513").build();
    ScanFilter scanFilter=new ScanFilter.Builder().setDeviceAddress("24:0A:C4:0D:4C:22").build();
    ScanFilter filter1=new ScanFilter.Builder().build();
//    list.add(filter);
//    list.add(filter1);
//    list.add(scanFilter);
    adapter.getBluetoothLeScanner().startScan(list,new ScanSettings.Builder().build(),mScanDeviceCallback);
    isScanning.set(true);
    mScanDeviceCallback.onScanStart(true);
  }
}
