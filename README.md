# Blelib
---
[![Build Status](https://travis-ci.org/NoHarry/BLELib.svg?branch=dev)](https://travis-ci.org/NoHarry/BLELib)
[ ![Download](https://api.bintray.com/packages/l2011louhanyu/maven/BleLib/images/download.svg) ](https://bintray.com/l2011louhanyu/maven/BleLib/_latestVersion)
<a href="https://play.google.com/store/apps/details?id=cc.noharry.bledemo">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a> |[中文文档](http://noharry.cc/2018/06/28/Blelib%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E/)

Blelib is an Android-side Bluetooth Low Energy library that contains many of the features you need to interact with BLE peripherals.

## Features
* Support basic functions such as scanning, connecting, reading, writing, and notification with peripheral devices
* Support custom scan
* Support custom connection timeout
* Supports connection of specified symbol rate PHY (Physical Layer) (PHY_LE_1M_MASK, PHY_LE_2M_MASK, PHY_LE_CODED_MASK)
* Support setting connection priority
* Support for setting the maximum transmission unit (MTU)
* Support log switch

## Configuration
### Gradle

```
// Add in the build.gradle file of your project's Project level
repositories {
  jcenter()
}

// Add the following dependencies to the build.gradle file
// at your project's Module level
dependencies {
    implementation 'cc.noharry.blelib:blelib:0.0.5'
}
```

### Maven
```
<dependency>
  <groupId>cc.noharry.blelib</groupId>
  <artifactId>blelib</artifactId>
  <version>0.0.5</version>
  <type>pom</type>
</dependency>
```
### Permission

```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```
The third permission in Android API 23 and above requires [Request permission at runtime](https://developer.android.com/training/permissions/requesting?hl=zh-cn)

**Note**: This library requires java1.8 version support

```
// Configure the following properties in the build.gradle file
// under your project's Module level.
android {
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
  }
```


## How to use
---

### 1.Scan
* Set scan rules

|method|description|
| ---                                               | ---                                                                            |
| setUUID(UUID[] uuid)| Filter out devices with input UUIDs in the AdvData
| setDeviceName(String[] deviceName,boolean fuzzzy) | Filter out the device with the input device name. The second parameter is true to indicate that the device name contains the name passed in. |
|setDeviceMac(String[] deviceMac)|Filter out the device with the input MAC address, for example, 11:22:33:44:55:66|
|setScanTime(long scanTime)|Set the scan duration in milliseconds; if you do not call this method or pass 0, it will enter continuous scan mode.|
```java
BleScanConfig scanConfig = new Builder()
       .setDeviceName(new String[]{Name}, isFuzzy)
       .setDeviceMac(new String[]{Mac})
       .setUUID(new UUID[]{UUID.fromString(uuid)})
       .setScanTime(scanTime)
       .build();
```
* Start scanning

```java
//Scan result callback
BleScanCallback mBleScanCallback = new BleScanCallback() {
      @Override
      public void onScanStarted(boolean isStartSuccess) {
        //Start scanning
      }

      @Override
      public void onFoundDevice(BleDevice bleDevice) {
        //Discover the device, this callback will continuously call back
        //the discovered device during the scanning process,
        // which will appear duplicate devices
        //Therefore, the user needs to filter according to their usage.
      }

      @Override
      public void onScanCompleted(List<BleDevice> deviceList) {
        //Scan completed
        //Callback will occur when the scan time ends
        //or when stopScan() is called actively

        //The device that is recalled is all the devices scanned in the process,
        //and the duplicate device has been filtered.
      }
    };
BleAdmin
      .getINSTANCE(getApplication())
      .scan(scanConfig, mBleScanCallback);

```
* Stop scanning

```java
BleAdmin.getINSTANCE(getApplication()).stopScan();
```
### 2.connection

* Connection callback

```java
BleConnectCallback mBleConnectCallback = new BleConnectCallback() {
      @Override
      public void onDeviceConnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceConnected(BleDevice bleDevice) {

      }

      @Override
      public void onServicesDiscovered(BleDevice bleDevice
      , BluetoothGatt gatt, int status) {

      }

      @Override
      public void onDeviceDisconnecting(BleDevice bleDevice) {

      }

      @Override
      public void onDeviceDisconnected(BleDevice bleDevice, int status) {

      }
    };
```
* Start connecting

|parameter|Required|description|
| --- |---| ---   |
| BleDevice  |true| Device to connect |
| isAutoConnect |false| Whether to use automatic connection, true: use automatic connection, the connection is slow; false: connect immediately |
|BaseBleConnectCallback|true|If you want to fully handle the connected callback yourself: BaseBleConnectCallback; if you just want to use it simply: BleConnectCallback|
|preferredPhy|false|Connection using the specified symbol rate PHY (Physical Layer) (PHY_LE_1M_MASK, PHY_LE_2M_MASK, PHY_LE_CODED_MASK)
|timeOut|false|Connection timeout in milliseconds|


```java
BleAdmin
          .getINSTANCE(getApplication())
          .connect(bleDevice
              , false
              , mBleConnectCallback
              ,timeOut);
```
* Disconnect

```java
BleAdmin
        .getINSTANCE(getApplication())
        .disconnect(bleDevice);
```
### 3.Read, write, notify, etc.
Because Android's operations on reading, writing, etc. of BLE devices need to be performed after the previous task is completed, the following tasks will be executed in order after they are created and added to the task queue.

#### 3.1 Read
* Create a read task

|parameter|Required|description|
|---|---|---|
|bleDevice|true|Read task execution device|
|bluetoothGattCharacteristic|true|Characteristic to read|
|bluetoothGattDescriptor|true|Descriptor to read|

  ```java
  ReadTask task = Task.newReadTask(bleDevice
  , characteristic)
      .with(mReadCallback);
  ```


* enqueue the task

  ```java
    BleAdmin.getINSTANCE(getApplication()).addTask(task);
  ```

* Result callback

  ```java
  ReadCallback mReadCallback = new ReadCallback() {
        @Override
        public void onDataRecived(BleDevice bleDevice, Data data) {
          //Readback of the data
        }

        @Override
        public void onOperationSuccess(BleDevice bleDevice) {
          //Successful operation
        }

        @Override
        public void onFail(BleDevice bleDevice, int statuCode, String message) {
          //Failure callback
        }

        @Override
        public void onComplete(BleDevice bleDevice) {
          //Complete callback
        }
      };
  ```



#### 3.2 Write

* Create a data object that needs to be written

|method|description|
|---|---|
|setValue(byte[] value)|Set the data to be written|
|setValue(byte[] value,boolean isAutoSplit)|Set the data to be written, and if the data length is greater than the MTU value, whether it is automatically subcontracted, the default is to send 20 bytes per packet.|
|setValue(List< byte[] > value)|If you want to subcontract yourself, you can call this method.|
|setMTUSize(int packSize)|If you have requested other MTU values, this method can change the size of the data sent per packet when auto-packing|

```java
WriteData writeData=new WriteData();
    writeData.setValue(data,true);
```

* Create a write task

|parameter|Required|description|
| --- | --- | --- |
|bleDevice|true|Devices that need to write data|
|characteristic|true|Characteristics that need to be written|
|bluetoothGattDescriptor|true|Descriptor of the data to be written|
|data|true|Data to be written|

```java
WriteTask task = Task.newWriteTask(bleDevice, characteristic, writeData)
    .with(mWriteCallback);
```

* enqueue the task

```java
BleAdmin.getINSTANCE(getApplication()).addTask(task);
```

* Result callback

```java
WriteCallback mWriteCallback = new WriteCallback() {
      @Override
      public void onDataSent(BleDevice bleDevice, Data data, int totalPackSize,
          int remainPackSize) {
              //bleDevice:Target device
              //data:Written data
              //totalPackSize：The total number of packets sent by this task
              //remainPackSize:Current remaining number of packages
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {

      }

      @Override
      public void onComplete(BleDevice bleDevice) {

      }
    };

```

#### 3.3 Notifications

* Create a notification task

```java
//Open notification
WriteTask enableTask = Task
.newEnableNotificationsTask(bleDevice, characteristic)
.with(mDataChangeCallback);

//Close notification
WriteTask disableTask = Task
.newDisableNotificationsTask(bleDevice, characteristic)
.with(mDataChangeCallback);
```
* enqueue the task


```java
BleAdmin.getINSTANCE(getApplication()).addTask(task);
```
* Result callback

```java
DataChangeCallback mDataChangeCallback = new DataChangeCallback() {
      @Override
      public void onDataChange(BleDevice bleDevice, Data data) {
        //This method will be called back when the notification is received
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {

      }

      @Override
      public void onComplete(BleDevice bleDevice) {

      }
    };
```

#### 3.4 Change the maximum transmission unit(MTU)

* Create task

```java
MtuTask mtuTask = Task
.newMtuTask(bleDevice, mtu)
.with(mMtuCallback);
```
* enqueue the task

```java
BleAdmin.getINSTANCE(getApplication()).addTask(task);
```

* Result callback

```java
MtuCallback mMtuCallback = new MtuCallback() {
      @Override
      public void onMtuChanged(BleDevice bleDevice, int mtu) {
          //This method will be called back after the MTU is successfully modified
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {

      }

      @Override
      public void onComplete(BleDevice bleDevice) {

      }
    };
```

#### 3.5 Change connection priority

* Create task

```java
ConnectionPriorityTask task = Task
.newConnectionPriorityTask(bleDevice, connectionPriority)
.with(mPriorityCallback);
```

* enqueue the task

```java
BleAdmin.getINSTANCE(getApplication()).addTask(task);
```

* Result callback

```java
ConnectionPriorityCallback priorityCallback = new ConnectionPriorityCallback() {
      @Override
      public void onConnectionUpdated(BleDevice bleDevice, int interval, int latency,
          int timeout, int status) {
        //This callback will only take effect in Android O and above.
      }

      @Override
      public void onOperationSuccess(BleDevice bleDevice) {

      }

      @Override
      public void onFail(BleDevice bleDevice, int statuCode, String message) {

      }

      @Override
      public void onComplete(BleDevice bleDevice) {

      }
    };
```
