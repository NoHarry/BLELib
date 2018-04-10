package cc.noharry.blelib.data;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/**
 * @author NoHarry
 * @date 2018/04/02
 */

public class BleDevice implements Parcelable{
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

  protected BleDevice(Parcel in) {
    mBluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    mScanRecord = in.createByteArray();
    mRssi = in.readInt();
    mTimestampNanos = in.readLong();
  }

  public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
    @Override
    public BleDevice createFromParcel(Parcel in) {
      return new BleDevice(in);
    }

    @Override
    public BleDevice[] newArray(int size) {
      return new BleDevice[size];
    }
  };

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(mBluetoothDevice,flags);
    dest.writeByteArray(mScanRecord);
    dest.writeInt(mRssi);
    dest.writeLong(mTimestampNanos);
  }


  @Override
  public String toString() {
    return "BleDevice{" +
        "mBluetoothDevice{name=" + mBluetoothDevice.getName() +
        ",mac="+ mBluetoothDevice.getAddress()+
        "}"+
        ", mScanRecord=" + Arrays.toString(mScanRecord) +
        ", mRssi=" + mRssi +
        ", mTimestampNanos=" + mTimestampNanos +
        '}';
  }
}
