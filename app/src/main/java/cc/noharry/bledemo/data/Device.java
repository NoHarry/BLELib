package cc.noharry.bledemo.data;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import cc.noharry.blelib.data.BleDevice;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class Device {
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
}
