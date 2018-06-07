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
  public ObservableInt state;
  public ObservableInt rssi;
  public BleDevice mBleDevice;
  public static final int CONNECTING=0;
  public static final int CONNECTED=1;
  public static final int DISCONNECTED=2;

  public Device(BleDevice bleDevice) {
    mBleDevice = bleDevice;
    key=new ObservableField<>(bleDevice.getKey());
    name=new ObservableField<>(bleDevice.getName());
    mac=new ObservableField<>(bleDevice.getMac());
    rssi=new ObservableInt(bleDevice.getRssi());
    state=new ObservableInt(DISCONNECTED);
  }


  protected Device(Parcel in) {
    state = in.readParcelable(ObservableInt.class.getClassLoader());
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

  public void setKey(String key) {
    this.key.set(key);
  }

  public ObservableField<String> getName() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public ObservableField<String> getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac.set(mac);
  }

  public ObservableInt getRssi() {
    return rssi;
  }

  public void setRssi(int rssi) {
    this.rssi.set(rssi);
  }

  public BleDevice getBleDevice() {
    return mBleDevice;
  }

  public void setBleDevice(BleDevice bleDevice) {
    mBleDevice = bleDevice;
  }

  public ObservableInt getState() {
    return state;
  }

  public void setState(int state) {
    this.state.set(state);
  }

  @Override
  public String toString() {
    return "Device{" +
        "key=" + key +
        ", name=" + name +
        ", mac=" + mac +
        ", state=" + state +
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
    dest.writeParcelable(state, flags);
    dest.writeParcelable(rssi, flags);
    dest.writeParcelable(mBleDevice, flags);
  }
}
