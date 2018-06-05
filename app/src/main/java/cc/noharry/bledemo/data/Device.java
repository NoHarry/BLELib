package cc.noharry.bledemo.data;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class Device implements Parcelable {
  public ObservableField<String> key;
  public ObservableField<String> name;
  public ObservableField<String> mac;
  public ObservableInt rssi;
  public BleDevice mBleDevice;

  public Device(BleDevice bleDevice) {
    mBleDevice = bleDevice;
    key=new ObservableField<>(bleDevice.getKey());
    name=new ObservableField<>(bleDevice.getName());
    mac=new ObservableField<>(bleDevice.getMac());
    rssi=new ObservableInt(bleDevice.getRssi());
  }

  protected Device(Parcel in) {
    rssi = in.readParcelable(ObservableInt.class.getClassLoader());
    mBleDevice = in.readParcelable(BleDevice.class.getClassLoader());
  }

  public static final Creator<Device> CREATOR = new Creator<Device>() {
    @Override
    public Device createFromParcel(Parcel in) {
      return new Device(in);
    }

    @Override
    public Device[] newArray(int size) {
      return new Device[size];
    }
  };

  public ObservableField<String> getKey() {
    return key;
  }

  public void setKey(ObservableField<String> key) {
    this.key = key;
  }

  public ObservableField<String> getName() {
    return name;
  }

  public void setName(ObservableField<String> name) {
    this.name = name;
  }

  public ObservableField<String> getMac() {
    return mac;
  }

  public void setMac(ObservableField<String> mac) {
    this.mac = mac;
  }

  public ObservableInt getRssi() {
    return rssi;
  }

  public void setRssi(ObservableInt rssi) {
    this.rssi = rssi;
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }

  public void setBleDevice(BleDevice bleDevice) {
    mBleDevice = bleDevice;
  }

  @Override
  public String toString() {
    return "Device{" +
        "key=" + key +
        ", name=" + name +
        ", mac=" + mac +
        ", rssi=" + rssi +
        ", mBleDevice=" + mBleDevice +
        '}';
  }

  @Override
  public boolean equals(Object obj) {
    Device device= (Device) obj;
    return key.get().equals(device.getKey().get());
  }

  @Override
  public int hashCode() {

    return key.get().hashCode();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(rssi, flags);
    dest.writeParcelable(mBleDevice, flags);
  }
}
