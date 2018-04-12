package cc.noharry.blelib.callback;

import android.content.Context;
import cc.noharry.blelib.ble.BleScanConfig;
import cc.noharry.blelib.ble.BleScanner;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
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

  public NearScanCallback(Context context,BleScanConfig config,BleScanCallback bleScanCallback,
      NearScanDeviceCallback nearScanDeviceCallback) {
    mBleScanCallback = bleScanCallback;
    mBleScanConfig = config;
    mNearScanDeviceCallback=nearScanDeviceCallback;
    mContext=context;
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
    mBleScanCallback.onFoundDevice(bleDevice);
    handleStoreDevice(bleDevice);
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
  private void handleStoreDevice(BleDevice bleDevice) {
    mBleDevices.offer(bleDevice);
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
