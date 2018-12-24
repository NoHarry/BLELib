package cc.noharry.blelib.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanner;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import cc.noharry.blelib.util.MethodUtils;
import cc.noharry.blelib.util.ThreadPoolProxy.LocalTheadFactory;
import cc.noharry.blelib.util.ThreadPoolProxyFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
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
  private ConcurrentLinkedQueue<BleDevice> mBleDevices;
  private NearLeScanDeviceCallback mNearLeScanDeviceCallback;
  private static final int LEGACY_SCAN=1;
  private static final int NEW_SCAN=2;
  private int scanMode=0;
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private ThreadFactory mThreadFactory;
  private ScheduledFuture<?> mSchedule;


  private void runOnUiThread(Runnable runnable){
    if (Looper.myLooper()==Looper.getMainLooper()){
      runnable.run();
    }else {
      mHandler.post(runnable);
    }
  }

  public NearScanCallback(Context context,BleScanConfig config,BleScanCallback bleScanCallback,
      NearScanDeviceCallback nearScanDeviceCallback) {
    mBleScanCallback = bleScanCallback;
    mBleScanConfig = config;
    mNearScanDeviceCallback=nearScanDeviceCallback;
    mContext=context;
    scanMode=NEW_SCAN;
    mThreadFactory = new LocalTheadFactory("timeTask");
    mExecutorService = new ScheduledThreadPoolExecutor(1,mThreadFactory);
  }

  public NearScanCallback(Context context,BleScanConfig config,BleScanCallback bleScanCallback,
      NearLeScanDeviceCallback nearScanDeviceCallback) {
    mBleScanCallback = bleScanCallback;
    mBleScanConfig = config;
    mNearLeScanDeviceCallback=nearScanDeviceCallback;
    mContext=context;
    scanMode=LEGACY_SCAN;
    mThreadFactory = new LocalTheadFactory("timeTask");
    mExecutorService = new ScheduledThreadPoolExecutor(1,mThreadFactory);
  }

  public void onScanStarted(boolean isStartSuccess){
    mBleScanCallback.onScanStarted(isStartSuccess);
    mStartTime = System.currentTimeMillis();
    mDeviceList = new ArrayList<>();
    mDeviceList.clear();
    mBleDevices=new ConcurrentLinkedQueue<>();
    mBleDevices.clear();
    if (mBleScanConfig.getScanTime()!=0){
      startTimeTask();
    }

  }

  public void onFoundDevice(BleDevice bleDevice){
    handleStoreDevice(mBleScanConfig,bleDevice);
  }


  public void onScanCompleted(List<BleDevice> deviceList){
    handleComplete(deviceList);
  }

  private void handleComplete(List<BleDevice> deviceList) {
    ThreadPoolProxyFactory.getScanThreadPoolProxy().execute(new Runnable() {
      @Override
      public void run() {
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
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              mBleScanCallback.onScanCompleted(deviceList);
            }
          });

        }
      }
    });

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

    ThreadPoolProxyFactory.getScanThreadPoolProxy().submit(new Runnable() {
      @Override
      public void run() {
        boolean checkBleDevice =
            scanMode==NEW_SCAN?
                MethodUtils.checkBleDeviceNew(bleScanConfig, bleDevice):
                MethodUtils.checkBleDevice(bleScanConfig,bleDevice);
        if (checkBleDevice){
          mBleDevices.offer(bleDevice);
          mBleScanCallback.onFoundDevice(bleDevice);
        }
      }
    });

  }

  private void startTimeTask(){
    mSchedule = mExecutorService.schedule(new Runnable() {
      @Override
      public void run() {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            BleScanner.getINSTANCE(mContext).stopScan();
            onScanCompleted(mDeviceList);
            L.e("stop scan");
          }
        });

      }
    }, mBleScanConfig.getScanTime(), TimeUnit.MILLISECONDS);

  }

  private void stopTimeTask(){
    if (mSchedule!=null){
      L.i("stopTimeTask");
      mSchedule.cancel(true);
    }
  }

}
