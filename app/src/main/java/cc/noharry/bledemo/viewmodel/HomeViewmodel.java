package cc.noharry.bledemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.databinding.ObservableBoolean;
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
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class HomeViewmodel extends AndroidViewModel {
  private final SingleLiveEvent<Device> foundDevice=new SingleLiveEvent<>();
  private final SingleLiveEvent<Log> mLog=new SingleLiveEvent<>();
  private final SingleLiveEvent<Device> currentDevice=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> currentDeviceState=new SingleLiveEvent<>();

  private final SingleLiveEvent<List<Device>> deviceList=new SingleLiveEvent<>();
  public final ObservableBoolean isScanning=new ObservableBoolean(false);
//  public final SingleLiveEvent<Boolean> isScanning=new SingleLiveEvent<>();
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private LogCallback mLogCallback;
  private BleScanCallback mBleScanCallback;
  private BleConnectCallback mBleConnectCallback;
  private List<Log> mLogList=new ArrayList<>();
  public static final int SCANNING=0;
  public static final int NOT_SCAN=1;
  private BaseBleConnectCallback mBaseBleConnectCallback;


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
//        mLogList.add(log);
      }
    };
    L.i("HomeViewmodel:"+this+" mLogCallback:"+mLogCallback);
    mBleScanCallback = new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {

        isScanning.set(true);
        L.i("onScanStarted:"+isScanning.get());
      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        foundDevice.setValue(device);
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {

        isScanning.set(false);
        L.i("onScanCompleted:"+isScanning.get());
      }
    };
  /*  mBleConnectCallback = new BleConnectCallback() {
      @Override
      public void onDeviceConnecting(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTING);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }

      @Override
      public void onDeviceConnected(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }

      }

      @Override
      public void onServicesDiscovered(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        device.setGatt(gatt);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }

      @Override
      public void onDeviceDisconnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceDisconnected(BleDevice bleDevice, int status) {
        Device device=new Device(bleDevice);
        device.setState(Device.DISCONNECTED);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }
    };*/

    mBaseBleConnectCallback = new BaseBleConnectCallback() {
      @Override
      public void onConnectionStateChangeBase(BleDevice bleDevice, BluetoothGatt gatt, int status,
          int newState) {

      }

      @Override
      public void onServicesDiscoveredBase(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        device.setGatt(gatt);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }

      @Override
      public void onDescriptorReadBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattDescriptor descriptor, int status) {

      }

      @Override
      public void onDescriptorWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattDescriptor descriptor, int status) {

      }

      @Override
      public void onCharacteristicWriteBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattCharacteristic characteristic, int status) {

      }

      @Override
      public void onCharacteristicReadBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattCharacteristic characteristic, int status) {

      }

      @Override
      public void onCharacteristicChangedBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattCharacteristic characteristic) {

      }

      @Override
      public void onReadRemoteRssiBase(BleDevice bleDevice, BluetoothGatt gatt, int rssi,
          int status) {

      }

      @Override
      public void onReliableWriteCompletedBase(BleDevice bleDevice, BluetoothGatt gatt,
          int status) {

      }

      @Override
      public void onMtuChangedBase(BleDevice bleDevice, BluetoothGatt gatt, int mtu, int status) {

      }

      @Override
      public void onPhyReadBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
          int status) {

      }

      @Override
      public void onPhyUpdateBase(BleDevice bleDevice, BluetoothGatt gatt, int txPhy, int rxPhy,
          int status) {

      }

      @Override
      public void onDeviceConnectingBase(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTING);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }

      @Override
      public void onDeviceConnectedBase(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }

      @Override
      public void onDeviceDisconnectingBase(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceDisconnectedBase(BleDevice bleDevice, int status) {
        Device device=new Device(bleDevice);
        device.setState(Device.DISCONNECTED);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }
      }
    };
  }

  public void clearLog(){
    mLogList.clear();
  }

  private boolean checkSameDevice(BleDevice bleDevice1,BleDevice bleDevice2){
    return (bleDevice1.getKey()!=null)&&(bleDevice2.getKey()!=null)
        &&(bleDevice1.getKey().equalsIgnoreCase(bleDevice2.getKey()));
  }



  public void scan(){
    if (!isScanning.get()){
      BleScanConfig config=new BleScanConfig.Builder().setScanTime(5000).build();
      if (BleAdmin.getINSTANCE(getApplication()).isEnable()){
        BleAdmin
            .getINSTANCE(getApplication())
            .setLogEnable(true)
            .setLogStyle(BleAdmin.LOG_STYLE_DEFAULT)
            .scan(config, mBleScanCallback);
      }
    }


  }

  public void stopScan(){
    BleAdmin.getINSTANCE(getApplication()).stopScan();
  }

  public void connect(Device device){
    if (BleAdmin.getINSTANCE(getApplication()).isEnable()){
      BleAdmin
          .getINSTANCE(getApplication())
          .connect(device.getBleDevice()
              , false
              , mBaseBleConnectCallback);
    }

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

  public SingleLiveEvent<Device> getCurrentDevice() {
    return currentDevice;
  }

  public SingleLiveEvent<Log> getLog() {
    return mLog;
  }

  public SingleLiveEvent<Device> getFoundDevice() {
    return foundDevice;
  }


  public SingleLiveEvent<List<Device>> getDeviceList() {
    return deviceList;
  }

  public List<Log> getLogList() {
    return mLogList;
  }
}
