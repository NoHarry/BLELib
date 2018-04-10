package cc.noharry.blelib.callback;

import android.content.Context;
import cc.noharry.blelib.ble.BLEAdmin;
import cc.noharry.blelib.ble.BleScanConfig;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
  private ConcurrentHashMap<BleDevice, Integer> mDeviceMap;
  private List<BleDevice> mDeviceList;

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
    mDeviceMap = new ConcurrentHashMap<>();
    mDeviceList = new ArrayList<>();
    mDeviceMap.clear();
    mDeviceList.clear();
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
       mBleScanCallback.onScanCompleted(deviceList);
    }
  }

  public void onScanFail(int statuCode){

  }

  public void onScanCancel(){
    stopTimeTask();

  }
  private void handleStoreDevice(BleDevice bleDevice) {
    if (mDeviceList.isEmpty()){
      mDeviceList.add(bleDevice);
    }else {
      updateDevice(bleDevice);
    }
  }

  private void updateDevice(BleDevice bleDevice){
    for (BleDevice b:mDeviceList){
      if ((b.getBluetoothDevice().getName().equals(bleDevice.getBluetoothDevice().getName()))
          &&(b.getBluetoothDevice().getName().equals(bleDevice.getBluetoothDevice().getName()))){
        mDeviceList.remove(b);
        mDeviceList.add(bleDevice);
      }
    }
  }

  private void startTimeTask(){
    mExecutorService = new ScheduledThreadPoolExecutor(1);
    mExecutorService.schedule(new Runnable() {
      @Override
      public void run() {
        BLEAdmin.getINSTANCE(mContext).stopScan();
        onScanCompleted(mDeviceList);
        L.e("定时停止");
      }
    },mBleScanConfig.getScanTime(),TimeUnit.MILLISECONDS);

  }

  private void stopTimeTask(){
    if (mExecutorService!=null){
      mExecutorService.shutdownNow();
    }
  }

}
