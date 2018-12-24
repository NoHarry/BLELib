package cc.noharry.blelib.ble.scan;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.ParcelUuid;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.callback.NearLeScanDeviceCallback;
import cc.noharry.blelib.callback.NearScanDeviceCallback;
import cc.noharry.blelib.util.L;
import cc.noharry.blelib.util.ThreadPoolProxyFactory;
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
  private NearLeScanDeviceCallback mNearLeScanDeviceCallback;

  private  BleScanner(Context context) {
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

  public synchronized void scan(final BleScanConfig config, final BleScanCallback callback){
    mScanDeviceCallback = new NearScanDeviceCallback(mContext,config,callback);
    mNearLeScanDeviceCallback = new NearLeScanDeviceCallback(config,callback,mContext);
    //Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP){
      try {
        handleScanNew(config);
      }catch (IllegalArgumentException e){
        L.e("isScanning:"+isScanning.get());
        callback.onScanFail(BleScanCallback.SCAN_FAIL_SCAN_WRONG_FILTER,e.getMessage());
        e.printStackTrace();
      }

      L.i("start new scan");
    }else {
        handleScan(config);
      L.i("start lagacy scan");
    }
  }

  private void handleScan(BleScanConfig config) {
    ThreadPoolProxyFactory.getSingleScanThreadPoolProxy().execute(new Runnable() {
      @Override
      public void run() {
        boolean startLeScan = BleAdmin.getINSTANCE(mContext).getBluetoothAdapter()
            .startLeScan(mNearLeScanDeviceCallback);
        isScanning.set(startLeScan);

      }
    });
    mNearLeScanDeviceCallback.onScanStart(isScanning.get());


    /*boolean startLeScan = BleAdmin.getINSTANCE(mContext).getBluetoothAdapter()
        .startLeScan(mNearLeScanDeviceCallback);
    isScanning.set(startLeScan);
    mNearLeScanDeviceCallback.onScanStart(isScanning.get());*/

  }

  public void stopScan(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
     BleAdmin.getINSTANCE(mContext).getBluetoothAdapter()
         .getBluetoothLeScanner().stopScan(mScanDeviceCallback);
     isScanning.set(false);
    }else {
      BleAdmin.getINSTANCE(mContext).getBluetoothAdapter()
          .stopLeScan(mNearLeScanDeviceCallback);
      isScanning.set(false);
    }
  }

  public void cancelScan(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
      if (mScanDeviceCallback!=null){
        mScanDeviceCallback.onScanCancel();
      }
    }else {
      if (mNearLeScanDeviceCallback!=null){
        mNearLeScanDeviceCallback.onScanCancel();
      }
    }
  }


  @TargetApi(VERSION_CODES.LOLLIPOP)
  private void handleScanNew(BleScanConfig config) {
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
    ThreadPoolProxyFactory.getSingleScanThreadPoolProxy().execute(new Runnable() {
      @Override
      public void run() {
        BleAdmin
            .getINSTANCE(mContext)
            .getBluetoothAdapter()
            .getBluetoothLeScanner()
            .startScan(list
                ,new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build()
                ,mScanDeviceCallback);
        isScanning.set(true);

      }
    });
    mScanDeviceCallback.onScanStart(isScanning.get());




    /*BleAdmin
        .getINSTANCE(mContext)
        .getBluetoothAdapter()
        .getBluetoothLeScanner()
        .startScan(list
            ,new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build()
            ,mScanDeviceCallback);
    isScanning.set(true);
    mScanDeviceCallback.onScanStart(isScanning.get());*/
  }
}
