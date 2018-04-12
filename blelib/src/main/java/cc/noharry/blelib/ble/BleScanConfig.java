package cc.noharry.blelib.ble;

import java.util.UUID;

/**
 * @author NoHarry
 * @date 2018/04/08
 */

public class BleScanConfig {
  private UUID[] mUUIDS;
  private String[] mDeviceNames;
  private String[] mDeviceMac;
  private boolean isAutoConnect =false;
  private boolean isFuzzy= false;
  private long mScanTime=0;


  public UUID[] getUUIDS() {
    return mUUIDS;
  }

  private void setUUIDS(UUID[] UUIDS) {
    mUUIDS = UUIDS;
  }

  public String[] getDeviceNames() {
    return mDeviceNames;
  }

  private void setDeviceNames(String[] deviceNames) {
    mDeviceNames = deviceNames;
  }

  public String[] getDeviceMac() {
    return mDeviceMac;
  }

  private void setDeviceMac(String[] deviceMac) {
    mDeviceMac = deviceMac;
  }

  public boolean isAutoConnect() {
    return isAutoConnect;
  }

  private void setAutoConnect(boolean autoConnect) {
    isAutoConnect = autoConnect;
  }

  public boolean isFuzzy() {
    return isFuzzy;
  }

  private void setFuzzy(boolean fuzzy) {
    isFuzzy = fuzzy;
  }

  public long getScanTime() {
    return mScanTime;
  }

  public void setScanTime(long scanTime) {
    mScanTime = scanTime;
  }

  public static class Builder{
    private UUID[] mUUIDS;
    private String[] mDeviceNames;
    private String[] mDeviceMac;
    private boolean isAutoConnect =false;
    private boolean isFuzzy= false;
    private long mScanTime=0;

    public Builder setUUID(UUID[] uuid){
      this.mUUIDS=uuid;
      return this;
    }

    public Builder setDeviceName(String[] deviceName,boolean fuzzzy){
      this.mDeviceNames=deviceName;
      this.isFuzzy=fuzzzy;
      return this;
    }

    public Builder setDeviceMac(String[] deviceMac){
      this.mDeviceMac = deviceMac;
      return this;
    }

    public Builder setIsAutoConnect(boolean isAutoConnect){
      this.isAutoConnect=isAutoConnect;
      return this;
    }

    public Builder setScanTime(long scanTime){
      this.mScanTime=scanTime;
      return this;
    }

    public BleScanConfig build(){
      BleScanConfig config=new BleScanConfig();
      config.setUUIDS(this.mUUIDS);
      config.setDeviceNames(this.mDeviceNames);
      config.setFuzzy(this.isFuzzy);
      config.setAutoConnect(this.isAutoConnect);
      config.setScanTime(this.mScanTime);
      config.setDeviceMac(this.mDeviceMac);

      return config;
    }

  }
}
