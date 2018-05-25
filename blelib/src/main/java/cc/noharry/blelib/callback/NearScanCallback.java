package cc.noharry.blelib.callback;

import android.content.Context;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanner;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import cc.noharry.blelib.util.MethodUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public  class NearScanCallback {
  private BleScanCallback mBleScanCallback;
  private BleScanConfig mBleScanConfig;
  private long mStartTime;
  private NearScanDeviceCallback mNearScanDeviceCallback;
  private Context mContext;
  private ScheduledExecutorService mExecutorService;
  private List<BleDevice> mDeviceList;
  private BlockingQueue<BleDevice> mBleDevices;
  private NearLeScanDeviceCallback mNearLeScanDeviceCallback;
  private static final int LEGACY_SCAN=1;
  private static final int NEW_SCAN=2;
  private int scanMode=0;

  public NearScanCallback(Context context,BleScanConfig config,BleScanCallback bleScanCallback,
      NearScanDeviceCallback nearScanDeviceCallback) {
    mBleScanCallback = bleScanCallback;
    mBleScanConfig = config;
    mNearScanDeviceCallback=nearScanDeviceCallback;
    mContext=context;
    scanMode=NEW_SCAN;
  }

  public NearScanCallback(Context context,BleScanConfig config,BleScanCallback bleScanCallback,
      NearLeScanDeviceCallback nearScanDeviceCallback) {
    mBleScanCallback = bleScanCallback;
    mBleScanConfig = config;
    mNearLeScanDeviceCallback=nearScanDeviceCallback;
    mContext=context;
    scanMode=LEGACY_SCAN;
  }

  public void onScanStarted(boolean isStartSuccess){
    mBleScanCallback.onScanStarted(isStartSuccess);
    mStartTime = System.currentTimeMillis();
    mDeviceList = new ArrayList<>();
    mDeviceList.clear();
    mBleDevices=new LinkedBlockingQueue<>();
    mBleDevices.clear();
    if (mBleScanConfig.getScanTime()!=0){
      startTimeTask();
    }

  }

  public void onFoundDevice(BleDevice bleDevice){
    handleStoreDevice(mBleScanConfig,bleDevice);
  }


  public void onScanCompleted(List<BleDevice> deviceList){
    if (mBleScanCallback!=null){
      Map<String,BleDevice> map=new HashMap<>();
        while (!mBleDevices.isEmpty()){
          BleDevice bleDevice = mBleDevices.poll();
          map.put(bleDevice.getBluetoothDevice().getAddress(),bleDevice);
        }
        List<String> mac=new ArrayList<>(map.keySet());
        for (String s:mac){
          mDeviceList.add(map.get(s));
        }
       mBleScanCallback.onScanCompleted(deviceList);
    }
  }

  public void onScanFail(int statuCode){

  }

  public void onScanCancel(){
    L.i("onScanCancel");
    if (BleScanner.isScanning.get()){
      stopTimeTask();
      BleScanner.getINSTANCE(mContext).stopScan();
      onScanCompleted(mDeviceList);
    }

  }

  private void handleStoreDevice(BleScanConfig bleScanConfig,
      BleDevice bleDevice) {
    boolean checkBleDevice =
        scanMode==NEW_SCAN?
            MethodUtils.checkBleDeviceNew(bleScanConfig, bleDevice):
            MethodUtils.checkBleDevice(bleScanConfig,bleDevice);
    if (checkBleDevice){
      mBleScanCallback.onFoundDevice(bleDevice);
      mBleDevices.offer(bleDevice);
    }
  }

  private void startTimeTask(){
    mExecutorService = new ScheduledThreadPoolExecutor(1);
    mExecutorService.schedule(new Runnable() {
      @Override
      public void run() {
        BleScanner.getINSTANCE(mContext).stopScan();
        onScanCompleted(mDeviceList);
        L.e("定时停止");
      }
    },mBleScanConfig.getScanTime(),TimeUnit.MILLISECONDS);

  }

  private void stopTimeTask(){
    if (mExecutorService!=null){
      L.i("stopTimeTask");
      mExecutorService.shutdownNow();
      mExecutorService=null;
    }
  }

}
