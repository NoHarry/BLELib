package cc.noharry.blelib.ble.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.MultipleBleController;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleGattCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/05/18
 */

public class BleClient implements IBleOperation{
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private BleDevice mBleDevice;
  private BluetoothGatt gatt;
  private BleConnectCallback mBleConnectCallback;
  private AtomicBoolean isConnected=new AtomicBoolean(false);
  private final BleConnectorProxy mBleConnectorProxy;

  public BleClient(BleDevice bleDevice) {
    mBleDevice = bleDevice;
    MultipleBleController.getInstance(BleAdmin.getContext()).getClientMap().put(getKey(),this);
//    BleAdmin.getINSTANCE(BleAdmin.getContext()).getMultipleBleController()
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public String getKey(){
    return mBleDevice.getKey();
  }

  public AtomicBoolean getIsConnected() {
    return isConnected;
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }


  @SuppressLint("NewApi")
  protected synchronized BluetoothGatt connect(boolean isAutoConnect,BleConnectCallback callback){
      return connect(isAutoConnect,BluetoothDevice.PHY_LE_1M_MASK,callback);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  protected synchronized BluetoothGatt connect(boolean isAutoConnect,int preferredPhy,BleConnectCallback callback){
    mBleConnectCallback=callback;
    L.i("connect");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBleGattCallback, BluetoothDevice.TRANSPORT_LE, preferredPhy, mHandler);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBleGattCallback, BluetoothDevice.TRANSPORT_LE);
    } else {
      gatt = mBleDevice.getBluetoothDevice().connectGatt(BleAdmin.getContext(),
          isAutoConnect, mBleGattCallback);
    }
    return gatt;
  }

  protected synchronized void disconnect(){
    if (gatt!=null){
      gatt.disconnect();
      L.i("disconnect():"+MultipleBleController.getInstance(BleAdmin.getContext()).getClientMap());
    }
  }

  @Override
  public String toString() {
    return "BleClient{" +
        "mBleDevice=" + mBleDevice +
        ", gatt=" + gatt +
        ", isConnected=" + isConnected.get() +
        '}';
  }

  private BleGattCallback mBleGattCallback=new BleGattCallback() {
    @Override
    public void onConnectionStateChangeMain(BluetoothGatt gatt, int status, int newState) {
      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){
        mBleConnectCallback.onDeviceConnected(getBleDevice());
        isConnected.set(true);
        BleClient.this.gatt.discoverServices();
        mBleConnectorProxy.taskNotify(status);
      }
      if (newState==BluetoothProfile.STATE_DISCONNECTED){
        isConnected.set(false);
      }
      L.i("onConnectionStateChangeMain"+" statu:"+status+" newState:"+newState);
    }

    @Override
    public void onServicesDiscoveredMain(BluetoothGatt gatt, int status) {
      mBleConnectCallback.onServicesDiscovered(getBleDevice());
      for (BluetoothGattService service:gatt.getServices()){
        L.i("onServicesDiscoveredMain"+" statu:"+status+" services:"+service.getUuid().toString());
      }


    }

    @Override
    public void onDescriptorReadMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      L.i("onDescriptorReadMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onDescriptorWriteMain(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      L.i("onDescriptorWriteMain"+" statu:"+status+" descriptor:"+descriptor);
    }

    @Override
    public void onCharacteristicWriteMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      L.i("onCharacteristicWriteMain"+" statu:"+status+" characteristic:"+characteristic);
    }

    @Override
    public void onCharacteristicReadMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      L.i("onCharacteristicReadMain"+" statu:"+status+" characteristic:"+ Arrays.toString(characteristic.getValue()));
        mBleConnectorProxy.taskNotify(status);
    }

    @Override
    public void onCharacteristicChangedMain(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      L.i("onCharacteristicChangedMain"+" characteristic:"+characteristic);
    }

    @Override
    public void onReadRemoteRssiMain(BluetoothGatt gatt, int rssi, int status) {
      L.i("onReadRemoteRssiMain"+" statu:"+status+" rssi:"+rssi);
    }

    @Override
    public void onReliableWriteCompletedMain(BluetoothGatt gatt, int status) {
      L.i("onReliableWriteCompletedMain"+" statu:"+status);
    }

    @Override
    public void onMtuChangedMain(BluetoothGatt gatt, int mtu, int status) {
      L.i("onMtuChangedMain"+" statu:"+status+" mtu:"+mtu);
    }

    @Override
    public void onPhyReadMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      L.i("onPhyReadMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
    }

    @Override
    public void onPhyUpdateMain(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
      L.i("onPhyUpdateMain"+" statu:"+status+" txPhy:"+txPhy+" rxPhy:"+rxPhy);
    }
  };

  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, BleConnectCallback callback) {
    connect(isAutoConnect, callback);
  }

  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void doConnect(BleDevice bleDevice, boolean isAutoConnect, int preferredPhy,
      BleConnectCallback callback) {
    connect(isAutoConnect, preferredPhy, callback);
  }

  @Override
  public void doDisconnect(BleDevice bleDevice) {
    disconnect();
  }

  @Override
  public void doTask(Task task) {
    L.i("doTask");
    if (isConnected.get()){
      handleTask(task);
    }else {
      mBleConnectorProxy.taskNotify(1);
      L.e("设备未连接");
    }

  }



  private void handleTask(Task task) {
    BluetoothGattService mBluetoothGattService;
    BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    if (task.isUseUUID){
      mBluetoothGattService=gatt.getService(UUID.fromString(task.mServiceUUID));
      mBluetoothGattCharacteristic=mBluetoothGattService.getCharacteristic(UUID.fromString(task.mCharacteristicUUID));
    }else {
      mBluetoothGattService=task.mBluetoothGattService;
      mBluetoothGattCharacteristic=task.mBluetoothGattCharacteristic;
    }
    switch (task.mType){
      case READ:
          gatt.readCharacteristic(mBluetoothGattCharacteristic);


        break;
      default:
    }
  }
}
