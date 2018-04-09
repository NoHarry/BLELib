package cc.noharry.blelib.data;

import android.bluetooth.BluetoothDevice;

/**
 * @author NoHarry
 * @date 2018/04/02
 */

public class BleDevice {
  private BluetoothDevice mBluetoothDevice;
  private byte[] mScanRecord;
  private int mRssi;
  private long mTimestampNanos;

  public BleDevice(BluetoothDevice bluetoothDevice, byte[] scanRecord, int rssi,
      long timestampNanos) {
    mBluetoothDevice = bluetoothDevice;
    mScanRecord = scanRecord;
    mRssi = rssi;
    mTimestampNanos = timestampNanos;
  }

  public BluetoothDevice getBluetoothDevice() {
    return mBluetoothDevice;
  }

  public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
    mBluetoothDevice = bluetoothDevice;
  }

  public byte[] getScanRecord() {
    return mScanRecord;
  }

  public void setScanRecord(byte[] scanRecord) {
    mScanRecord = scanRecord;
  }

  public int getRssi() {
    return mRssi;
  }

  public void setRssi(int rssi) {
    mRssi = rssi;
  }

  public long getTimestampNanos() {
    return mTimestampNanos;
  }

  public void setTimestampNanos(long timestampNanos) {
    mTimestampNanos = timestampNanos;
  }
}
