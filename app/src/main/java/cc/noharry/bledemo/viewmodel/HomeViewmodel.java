package cc.noharry.bledemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.util.Log;
import cc.noharry.bledemo.util.LogUtil;
import cc.noharry.bledemo.util.LogUtil.LogCallback;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.BleAdmin.OnBTOpenStateListener;
import cc.noharry.blelib.ble.connect.ConnectionPriorityTask;
import cc.noharry.blelib.ble.connect.MtuTask;
import cc.noharry.blelib.ble.connect.ReadTask;
import cc.noharry.blelib.ble.connect.Task;
import cc.noharry.blelib.ble.connect.WriteTask;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.ble.scan.BleScanConfig.Builder;
import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.callback.ConnectionPriorityCallback;
import cc.noharry.blelib.callback.DataChangeCallback;
import cc.noharry.blelib.callback.MtuCallback;
import cc.noharry.blelib.callback.ReadCallback;
import cc.noharry.blelib.callback.WriteCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.data.WriteData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class HomeViewmodel extends AndroidViewModel {
  private final SingleLiveEvent<Device> foundDevice=new SingleLiveEvent<>();
  private final SingleLiveEvent<Log> mLog=new SingleLiveEvent<>();
  private final SingleLiveEvent<Device> currentDevice=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> currentDeviceState=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> logSize=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> detailLogSize=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> detailLogClean=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> homeLogClean=new SingleLiveEvent<>();
  private final SingleLiveEvent<Integer> locationStatu=new SingleLiveEvent<>();
  private final SingleLiveEvent<List<Device>> deviceList=new SingleLiveEvent<>();
  public final ObservableBoolean isScanning=new ObservableBoolean(false);
  private final SingleLiveEvent<Integer> scanState=new SingleLiveEvent<>();
  public final ObservableBoolean isDialogShowing=new ObservableBoolean(false);
  private final SingleLiveEvent<Integer> valueChange=new SingleLiveEvent<>();
//  public final SingleLiveEvent<Boolean> isScanning=new SingleLiveEvent<>();
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private LogCallback mLogCallback;
  private BleScanCallback mBleScanCallback;
  private BleConnectCallback mBleConnectCallback;
  private List<Log> mLogList=new ArrayList<>();
  public static final int SCANNING=0;
  public static final int NOT_SCAN=1;
  private BaseBleConnectCallback mBaseBleConnectCallback;
  private WriteCallback mWriteCallback;
  private DataChangeCallback mDataChangeCallback;
  private ReadCallback mReadCallback;
  private String filterMac;
  private String filterName;
  private String filterUUID;
  private MtuCallback mMtuCallback;
  private ConnectionPriorityCallback mPriorityCallback;


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
    displayLog();
  }

  private void initCallback() {
    mLogCallback = new LogCallback() {
      @Override
      public void onLog(Log log) {
        /*if (isDialogShowing.get()){
          runOnUiThread(()->mLog.setValue(log));
        }*/
        mLogList.add(log);
        runOnUiThread(()->{
          logSize.setValue(mLogList.size());
          detailLogSize.setValue(mLogList.size());
        });

      }
    };
    mBleScanCallback = new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {

        isScanning.set(true);
        scanState.setValue(SCANNING);
        L.i("onScanStarted:"+isScanning.get());
      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        Device device=new Device(bleDevice);
        foundDevice.setValue(device);
//        L.i("onFoundDevice:"+bleDevice);
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {

        isScanning.set(false);
        scanState.setValue(NOT_SCAN);
        L.i("onScanCompleted:"+isScanning.get());
      }
    };


    mBleConnectCallback = new BleConnectCallback() {
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
        /*Device device=new Device(bleDevice);
        device.setState(Device.CONNECTED);
        foundDevice.setValue(device);
        try {
          if (checkSameDevice(bleDevice,currentDevice.getValue().getBleDevice())){
            currentDevice.setValue(device);
          }
        }catch (NullPointerException e){
        }*/

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
    };

    /*mBaseBleConnectCallback = new BaseBleConnectCallback() {
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
        L.i("onCharacteristicWriteBase:"+Arrays.toString(characteristic.getValue()));
        valueChange.setValue(0);
      }

      @Override
      public void onCharacteristicReadBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattCharacteristic characteristic, int status) {
        L.i("onCharacteristicReadBase:"+Arrays.toString(characteristic.getValue()));
        valueChange.setValue(0);
      }

      @Override
      public void onCharacteristicChangedBase(BleDevice bleDevice, BluetoothGatt gatt,
          BluetoothGattCharacteristic characteristic) {
        L.i("onCharacteristicChangedBase:"+Arrays.toString(characteristic.getValue()));
        valueChange.setValue(0);
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
        L.e("连接中:"+foundDevice.getValue());
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
        L.e("断开连接:"+foundDevice.getValue());
      }
    };*/

    mWriteCallback = new WriteCallback() {
      @Override
      public void onDataSent(BleDevice bleDevice, Data data, int totalPackSize,
          int remainPackSize) {
        L.i("onDataSent:" + data.toString()+" totalPackSize:"+totalPackSize+" remainPackSize:"+remainPackSize);
        valueChange.setValue(0);
      }



      @Override
      public void onOperationSuccess(BleDevice bleDevice) {
        L.i("onOperationSuccess");
      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {
        L.e("onFail:" + message);
      }

      @Override
      public void onComplete(BleDevice bleDevice) {
        L.i("onComplete");
      }
    };

    mDataChangeCallback = new DataChangeCallback() {
      @Override
      public void onDataChange(BleDevice bleDevice, Data data) {
        L.i("onDataChange:" + data.toString());
        valueChange.setValue(0);
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {

      }

      @Override
      public void onComplete(BleDevice bleDevice) {

      }
    };

    mReadCallback = new ReadCallback() {
      @Override
      public void onDataRecived(BleDevice bleDevice, Data data) {
        L.i("onDataRecived:" + data.toString());
        valueChange.setValue(0);
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {
        L.i("onOperationSuccess");

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {
        L.e("onFail:" + message);
      }

      @Override
      public void onComplete(BleDevice bleDevice) {
        L.i("onComplete");
      }
    };

    mMtuCallback = new MtuCallback() {
      @Override
      public void onMtuChanged(BleDevice bleDevice, int mtu) {
        L.i("onMtuChanged " + "bleDevice:" + bleDevice + " mtu:" + mtu);
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {
        L.i("onOperationSuccess " + "bleDevice:" + bleDevice);
      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {
        L.i("onFail " + "bleDevice:" + bleDevice + " message:" + message);
      }

      @Override
      public void onComplete(BleDevice bleDevice) {
        L.i("onComplete " + "bleDevice:" + bleDevice);
      }
    };

    mPriorityCallback = new ConnectionPriorityCallback() {
      @Override
      public void onConnectionUpdated(BleDevice bleDevice, int interval, int latency,
          int timeout, int status) {
        L.i("onConnectionUpdated:" + " interval:" + interval + " latency:" + latency
            + " timeout:" + timeout + " status:" + status);
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {
        L.i("onOperationSuccess:" + "ConnectionPriority");
      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {
        L.i("onOperationSuccess:" + "onFail" + message);
      }

      @Override
      public void onComplete(BleDevice bleDevice) {
        L.i("onOperationSuccess:" + "onComplete");
      }
    };
  }

  public void clearLog(){
    mLogList.clear();
    detailLogClean.setValue(mLogList.size());
    homeLogClean.setValue(mLogList.size());
  }


  public void read(Device device,BluetoothGattCharacteristic characteristic){
    ReadTask task = Task.newReadTask(device.getBleDevice(), characteristic)
        .with(mReadCallback);
    BleAdmin.getINSTANCE(getApplication()).addTask(task);
  }

  public void write(Device device,BluetoothGattCharacteristic characteristic,byte[] data){
    WriteData writeData=new WriteData();
    writeData.setValue(data,true);
    writeData.setMTUSize(517);
//    writeData.setValue(data);
    WriteTask task = Task.newWriteTask(device.getBleDevice(), characteristic, writeData)
        .with(mWriteCallback);
    BleAdmin.getINSTANCE(getApplication()).addTask(task);
  }

  private boolean checkSameDevice(BleDevice bleDevice1,BleDevice bleDevice2){
    return (bleDevice1.getKey()!=null)&&(bleDevice2.getKey()!=null)
        &&(bleDevice1.getKey().equalsIgnoreCase(bleDevice2.getKey()));
  }

  public void getConnectDevice(){
    List<BleDevice> connectedDevice = BleAdmin.getINSTANCE(getApplication()).getConnectedDevice();
    L.i("connectedDevice:"+connectedDevice);
  }

  public void disconnectAllDevices(){
    BleAdmin.getINSTANCE(getApplication()).disconnectAllDevices();
  }

  public void enableNotify(Device device,BluetoothGattCharacteristic characteristic){
    WriteTask enableTask = Task.newEnableNotificationsTask(device.getBleDevice(), characteristic)
        .with(mDataChangeCallback);
    BleAdmin.getINSTANCE(getApplication()).addTask(enableTask);
  }
  public void disableNotify(Device device,BluetoothGattCharacteristic characteristic){
    WriteTask disableTask = Task.newDisableNotificationsTask(device.getBleDevice(), characteristic)
        .with(mDataChangeCallback);
    BleAdmin.getINSTANCE(getApplication()).addTask(disableTask);

  }

  public void changeConnectionPriority(Device device,int connectionPriority){
    ConnectionPriorityTask task = Task.newConnectionPriorityTask(device.getBleDevice(), connectionPriority).with(
      mPriorityCallback   );
    BleAdmin.getINSTANCE(getApplication()).addTask(task);
  }

  public void scan(){
    if (!isScanning.get()&&checkLocation()){
//      BleScanConfig config=new BleScanConfig.Builder().setScanTime(5000).build();
      if (BleAdmin.getINSTANCE(getApplication()).isEnable()){
        BleAdmin
            .getINSTANCE(getApplication())
            .setLogEnable(true)
            .setLogStyle(BleAdmin.LOG_STYLE_DEFAULT)
            .scan(getConfig(), mBleScanCallback);
      }else {
        BleAdmin.getINSTANCE(getApplication()).openBT(new OnBTOpenStateListener() {
          @Override
          public void onBTOpen() {
            BleAdmin
                .getINSTANCE(getApplication())
                .setLogEnable(true)
                .setLogStyle(BleAdmin.LOG_STYLE_DEFAULT)
                .scan(getConfig(), mBleScanCallback);
          }
        });
      }
    }


  }

  private BleScanConfig getConfig(){
    Builder builder = new Builder();
    if (!TextUtils.isEmpty(filterMac)){
      builder.setDeviceMac(new String[]{filterMac});
    }
    if (!TextUtils.isEmpty(filterUUID)){
      builder.setUUID(new UUID[]{UUID.fromString(filterUUID)});
    }
    if (!TextUtils.isEmpty(filterName)){
      builder.setDeviceName(new String[]{filterName},true);
    }
    builder.setScanTime(5000);
    return builder.build();
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
              , mBleConnectCallback,1000*10);
    }

  }

  public void changeMtu(Device device,int mtu){
    MtuTask mtuTask = Task.newMtuTask(device.getBleDevice(), mtu).with(mMtuCallback);
    BleAdmin.getINSTANCE(getApplication()).addTask(mtuTask);
  }


  public boolean checkLocation(){
    LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
    if (!locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER)){
      locationStatu.setValue(1);
      return false;
    }else {
      locationStatu.setValue(0);
      return true;
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
//    LogUtil.readLog(null);
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


  public SingleLiveEvent<Integer> getLogSize() {
    return logSize;
  }

  public SingleLiveEvent<Integer> getScanState() {
    return scanState;
  }

  public SingleLiveEvent<Integer> getValueChange() {
    return valueChange;
  }

  public SingleLiveEvent<Integer> getDetailLogSize() {
    return detailLogSize;
  }

  public SingleLiveEvent<Integer> getDetailLogClean() {
    return detailLogClean;
  }

  public SingleLiveEvent<Integer> getHomeLogClean() {
    return homeLogClean;
  }

  public SingleLiveEvent<Integer> getLocationStatu() {
    return locationStatu;
  }

  public List<Log> getLogList() {
    return mLogList;
  }

  public void setFilter(String name,String mac,String uuid){
    filterMac=mac;
    filterName=name;
    filterUUID=uuid;
  }
}
