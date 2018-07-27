package cc.noharry.blelib.ble.connect;

import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.ConnectionPriorityCallback;
import cc.noharry.blelib.callback.TaskCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/07/05
 */
public class ConnectionPriorityTask extends Task {
  private int mConnectionPriority;
  private ConnectionPriorityCallback mConnectionPriorityCallback;
  private BleConnectorProxy mBleConnectorProxy;

  protected ConnectionPriorityTask(Type type, BleDevice bleDevice,int connectionPriority) {
    super(type, bleDevice);
    mConnectionPriority=connectionPriority;
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
  }

  public ConnectionPriorityTask with(ConnectionPriorityCallback callback){
    mConnectionPriorityCallback=callback;
    return this;
  }

  @Override
  public Task with(TaskCallback callback) {
    return super.with(callback);
  }

  @Override
  public Task setTaskTimeOut(long taskTimeOut){
    return super.setTaskTimeOut(taskTimeOut);
  }

  @Override
  protected void notitySuccess(BleDevice bleDevice) {
    mBleConnectorProxy.taskNotify(0);
    if (mConnectionPriorityCallback!=null){
      mConnectionPriorityCallback.onOperationSuccess(bleDevice);
      mConnectionPriorityCallback.onComplete(bleDevice);
    }


  }

  @Override
  protected void notifyError(BleDevice bleDevice, int statuCode) {
    mBleConnectorProxy.taskNotify(statuCode);
    if (mConnectionPriorityCallback!=null){
      mConnectionPriorityCallback.onFail(bleDevice,statuCode,GattError.parse(statuCode));
      mConnectionPriorityCallback.onComplete(bleDevice);
    }


  }

  @RequiresApi(api = VERSION_CODES.O)
  protected void notifyConnectionUpdated(BleDevice bleDevice, int interval, int latency, int timeout,
      int status){
    if (mConnectionPriorityCallback!=null){
      mConnectionPriorityCallback.onConnectionUpdated(bleDevice, interval, latency, timeout, status);
    }
  }

  public int getConnectionPriority() {
    return mConnectionPriority;
  }
}
