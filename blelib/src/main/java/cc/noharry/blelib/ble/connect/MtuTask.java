package cc.noharry.blelib.ble.connect;

import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.callback.MtuCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.exception.GattError;

/**
 * @author NoHarry
 * @date 2018/06/29
 */
public class MtuTask extends Task {
  private MtuCallback mMtuCallback;
  private BleConnectorProxy mBleConnectorProxy;
  private int mMtu;
  private long mTaskTimeOut = Task.NO_TIME_OUT;

  protected MtuTask(Type type, BleDevice bleDevice, int mtu) {
    super(type, bleDevice, mtu);
    mBleConnectorProxy = BleConnectorProxy.getInstance(BleAdmin.getContext());
    setMtu(mtu);
  }

  public MtuTask with(MtuCallback callback){
    mMtuCallback=callback;
    return this;
  }

  @Override
  public MtuTask setTaskTimeOut(long taskTimeOut){
    mTaskTimeOut=taskTimeOut;
    return this;
  }

  @Override
  protected void notitySuccess(BleDevice bleDevice) {
    mBleConnectorProxy.taskNotify(0);
    if (mMtuCallback!=null){
      mMtuCallback.onOperationSuccess(bleDevice);
    }
  }

  @Override
  protected void notifyError(BleDevice bleDevice, int statuCode) {
    mBleConnectorProxy.taskNotify(statuCode);
    if (mMtuCallback!=null){
      mMtuCallback.onFail(bleDevice,statuCode,GattError.parse(statuCode));
      mMtuCallback.onComplete(bleDevice);
    }
  }

  protected void notifyMtuChanged(BleDevice bleDevice,int mtu){
    if (mMtuCallback!=null){
      mMtuCallback.onMtuChanged(bleDevice, mtu);
      mMtuCallback.onComplete(bleDevice);
    }
  }

  public int getMtu() {
    return mMtu;
  }

  public void setMtu(int mtu) {
    if (mtu<23){
      mMtu=23;
    }else if (mtu>517){
      mMtu=517;
    }else {
      mMtu=mtu;
    }
  }

  @Override
  public long getTaskTimeOut() {
    return mTaskTimeOut;
  }
}
