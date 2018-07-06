package cc.noharry.blelib.ble.connect;

import cc.noharry.blelib.callback.BaseBleConnectCallback;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/07/06
 */
public class ConnectionRequest {
  public static final int CONNECTION_NORMAL=0;
  public static final int CONNECTION_NORMAL_WITH_TIMEOUT=1;
  public static final int CONNECTION_O=2;
  public static final int CONNECTION_O_WITH_TIMEOUT=3;

  private BleDevice mBleDevice;
  private boolean mIsAutoConnect;
  private BaseBleConnectCallback mBaseBleConnectCallback;
  private int mPreferredPhy;
  private long mTimeOut;
  private int mType;

  public ConnectionRequest(BleDevice bleDevice, boolean isAutoConnect,
      BaseBleConnectCallback baseBleConnectCallback, int preferredPhy, long timeOut) {
    mBleDevice = bleDevice;
    mIsAutoConnect = isAutoConnect;
    mBaseBleConnectCallback = baseBleConnectCallback;
    mPreferredPhy = preferredPhy;
    mTimeOut = timeOut;
    mType=CONNECTION_O_WITH_TIMEOUT;
  }

  public ConnectionRequest(BleDevice bleDevice, boolean isAutoConnect,
      BaseBleConnectCallback baseBleConnectCallback, long timeOut) {
    mBleDevice = bleDevice;
    mIsAutoConnect = isAutoConnect;
    mBaseBleConnectCallback = baseBleConnectCallback;
    mTimeOut = timeOut;
    mType=CONNECTION_NORMAL_WITH_TIMEOUT;
  }

  public ConnectionRequest(BleDevice bleDevice, boolean isAutoConnect,
      BaseBleConnectCallback baseBleConnectCallback) {
    mBleDevice = bleDevice;
    mIsAutoConnect = isAutoConnect;
    mBaseBleConnectCallback = baseBleConnectCallback;
    mType=CONNECTION_NORMAL;
  }

  public ConnectionRequest(BleDevice bleDevice, boolean isAutoConnect,
      BaseBleConnectCallback baseBleConnectCallback, int preferredPhy) {
    mBleDevice = bleDevice;
    mIsAutoConnect = isAutoConnect;
    mBaseBleConnectCallback = baseBleConnectCallback;
    mPreferredPhy = preferredPhy;
    mType=CONNECTION_O;
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }

  public void setBleDevice(BleDevice bleDevice) {
    mBleDevice = bleDevice;
  }

  public boolean isAutoConnect() {
    return mIsAutoConnect;
  }

  public void setAutoConnect(boolean autoConnect) {
    mIsAutoConnect = autoConnect;
  }

  public BaseBleConnectCallback getBaseBleConnectCallback() {
    return mBaseBleConnectCallback;
  }

  public void setBaseBleConnectCallback(
      BaseBleConnectCallback baseBleConnectCallback) {
    mBaseBleConnectCallback = baseBleConnectCallback;
  }

  public int getPreferredPhy() {
    return mPreferredPhy;
  }

  public void setPreferredPhy(int preferredPhy) {
    mPreferredPhy = preferredPhy;
  }

  public long getTimeOut() {
    return mTimeOut;
  }

  public void setTimeOut(long timeOut) {
    mTimeOut = timeOut;
  }

  public int getType() {
    return mType;
  }

  public void setType(int type) {
    mType = type;
  }

  @Override
  public String toString() {
    return "ConnectionRequest{" +
        "mBleDevice=" + mBleDevice +
        ", mIsAutoConnect=" + mIsAutoConnect +
        ", mBaseBleConnectCallback=" + mBaseBleConnectCallback +
        ", mPreferredPhy=" + mPreferredPhy +
        ", mTimeOut=" + mTimeOut +
        ", mType=" + mType +
        '}';
  }
}
