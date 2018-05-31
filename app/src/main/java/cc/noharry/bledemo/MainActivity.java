package cc.noharry.bledemo;

import android.bluetooth.BluetoothGatt;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import cc.noharry.bledemo.databinding.ActivityMainBinding;
import cc.noharry.blelib.ble.BleAdmin;
import cc.noharry.blelib.ble.connect.ReadTask;
import cc.noharry.blelib.ble.connect.Task;
import cc.noharry.blelib.ble.connect.WriteTask;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.callback.BleConnectCallback;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.callback.DataChangeCallback;
import cc.noharry.blelib.callback.ReadCallback;
import cc.noharry.blelib.callback.WriteCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.Data;
import cc.noharry.blelib.util.L;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

  private Handler mHandler=new Handler();
  private ActivityMainBinding mBinding;
  private List<BleDevice> mBleDevices=new ArrayList<>();
  private BleAdmin mBleAdmin;
  private BleDevice mBleDevice;
  private BleDevice mDevice1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    initData();
    initEvent();

  }

  private void initData() {
    mBleAdmin = BleAdmin.getINSTANCE(getApplicationContext());
  }

  private void initEvent() {
    mBinding.btnScan.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //0000ffe0-0000-1000-8000-00805f9b34fb
        UUID[] uuids=new UUID[]{UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"),UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")};
        BleScanConfig config=new BleScanConfig.Builder()
//            .setUUID(uuids)
//            .setScanTime(5000)
//            .setDeviceName(new String[]{"LL0835","android"},true)
//            .setDeviceMac(new String[]{"00:0E:0B:14:70:DE","24:0A:C4:10:A2:22"})
            .build();
//        BleAdmin.getINSTANCE(getApplication())

        mBleAdmin.scan(
            config
            , new BleScanCallback() {
          @Override
          public void onScanStarted(boolean isStartSuccess) {
            L.i("onScanStarted:"+isStartSuccess);
          }

          @Override
          public void onFoundDevice(BleDevice bleDevice) {
            L.i("onFoundDevice:"+bleDevice);
          }

          @Override
          public void onScanCompleted(List<BleDevice> deviceList) {
            L.i("onScanCompleted:"+deviceList);
            mBleDevices=deviceList;
          }
        });

      }
    });
    mBinding.btnStopScan.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mBleAdmin.stopScan();
      }
    });

    mBinding.btnConnect.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mBleDevice = null;
        for (BleDevice device:mBleDevices){
          if ("android test".equalsIgnoreCase(device.getName())){
            mBleDevice =device;
          }
          if ("android test1".equalsIgnoreCase(device.getName())){
            mDevice1 = device;
          }
        }
        if (mBleDevice !=null){
          new Thread(new Runnable() {
            @Override
            public void run() {
              mBleAdmin.connect(mBleDevice, false,
                  new BleConnectCallback() {
                    @Override
                    public void onDeviceConnecting(BleDevice bleDevice) {
                      L.i("onDeviceConnecting "+bleDevice);
                    }

                    @Override
                    public void onDeviceConnected(BleDevice bleDevice) {
                      L.i("onDeviceConnected "+bleDevice);
                    }

                    @Override
                    public void onServicesDiscovered(BleDevice bleDevice, BluetoothGatt gatt,
                        int status) {
                      L.i("onServicesDiscovered "+bleDevice);
                    }

                    @Override
                    public void onDeviceDisconnecting(BleDevice bleDevice) {
                      L.i("onDeviceDisconnecting "+bleDevice);
                    }

                    @Override
                    public void onDeviceDisconnected(BleDevice bleDevice,int status) {
                      L.i("onDeviceDisconnected "+bleDevice);
                    }
                  });
            }
          }).start();

        }
        if (mDevice1!=null){
          mBleAdmin.connect(mDevice1, false, new BleConnectCallback() {
            @Override
            public void onDeviceConnecting(BleDevice bleDevice) {
              L.i("onDeviceConnecting1 "+bleDevice);
            }

            @Override
            public void onDeviceConnected(BleDevice bleDevice) {
              L.i("onDeviceConnected1 "+bleDevice);
            }

            @Override
            public void onServicesDiscovered(BleDevice bleDevice, BluetoothGatt gatt, int status) {
              L.i("onServicesDiscovered1 "+bleDevice);
            }

            @Override
            public void onDeviceDisconnecting(BleDevice bleDevice) {
              L.i("onDeviceDisconnecting1 "+bleDevice);
            }

            @Override
            public void onDeviceDisconnected(BleDevice bleDevice,int status) {
              L.i("onDeviceDisconnected1 "+bleDevice);
            }
          });
        }


      }
    });


    mBinding.btnDisconnect.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mBleDevice!=null){
          new Thread(new Runnable() {
            @Override
            public void run() {
              mBleAdmin.disconnect(mBleDevice);
            }
          }).start();

        }
        if (mDevice1!=null){
          mBleAdmin.disconnect(mDevice1);
        }
      }
    });

    mBinding.btnRead.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        ReadTask readTask = Task.newReadTask(mBleDevice,"0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb").with(new ReadCallback() {
          @Override
          public void onDataRecived(BleDevice bleDevice, Data data) {
            L.i("onDataRecived"+" device:"+bleDevice+" data:"+ Arrays.toString(data.getValue()));
          }

          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSuccess"+" device:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail"+" device:"+bleDevice+" statuCode:"+statuCode+" message:"+ message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete"+" device:"+bleDevice);
          }
        });
        ReadTask readTask1 = Task.newReadTask(mDevice1,"0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb").with(new ReadCallback() {
          @Override
          public void onDataRecived(BleDevice bleDevice, Data data) {
            L.i("onDataRecived1"+" device:"+bleDevice+" data:"+ Arrays.toString(data.getValue()));
          }


          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSuccess1"+" device:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail1"+" device:"+bleDevice+" statuCode:"+statuCode+" message:"+ message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete1"+" device:"+bleDevice);
          }
        });
        if (mBleDevice!=null){
          mBleAdmin.addTask(readTask);
        }

//        mBleAdmin.addTask(readTask1);
        if (mDevice1!=null){
          new Thread(new Runnable() {
            @Override
            public void run() {
              mBleAdmin.addTask(readTask1);
            }
          }).start();
        }


      }
    });

    mBinding.btnWrite.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        WriteTask writeTask = Task.newWriteTask(mBleDevice, "0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb", "abcd".getBytes()).with(new WriteCallback() {
          @Override
          public void onDataSent(BleDevice bleDevice, Data data) {
            L.i("onDataSent "+" bleDevice:"+bleDevice+" data:"+Arrays.toString(data.getValue()));
          }

          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSuccess "+" bleDevice:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail "+" bleDevice:"+bleDevice+" statuCode:"+statuCode+" message:"+message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete"+" device:"+bleDevice);
          }
        });
        WriteTask writeTask1 = Task.newWriteTask(mDevice1, "0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb", "abcd".getBytes()).with(new WriteCallback() {
          @Override
          public void onDataSent(BleDevice bleDevice, Data data) {
            L.i("onDataSent1 "+" bleDevice:"+bleDevice+" data:"+Arrays.toString(data.getValue()));
          }

          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSucces1 "+" bleDevice:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail1 "+" bleDevice:"+bleDevice+" statuCode:"+statuCode+" message:"+message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete1"+" device:"+bleDevice);
          }
        });

        if (mBleDevice!=null){
          mBleAdmin.addTask(writeTask);
        }
        if (mDevice1!=null){
          mBleAdmin.addTask(writeTask1);
        }
      }
    });

    mBinding.btnEnableNotify.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        WriteTask enableTask = Task.newEnableNotificationsTask(mBleDevice, "0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb").with(new DataChangeCallback() {
          @Override
          public void onDataChange(BleDevice bleDevice, Data data) {
            L.i("onDataChange "+" bleDevice:"+bleDevice+" data:"+Arrays.toString(data.getValue()));
          }

          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSuccess "+" bleDevice:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail "+" bleDevice:"+bleDevice+" statuCode:"+statuCode+" message:"+message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete "+" bleDevice:"+bleDevice);
          }
        });
        WriteTask enableTask1 = Task.newEnableNotificationsTask(mDevice1, "0000ffe5-0000-1000-8000-00805f9b34fb",
            "0000ffe2-0000-1000-8000-00805f9b34fb").with(new DataChangeCallback() {
          @Override
          public void onDataChange(BleDevice bleDevice, Data data) {
            L.i("onDataChange1 "+" bleDevice:"+bleDevice+" data:"+Arrays.toString(data.getValue()));
          }

          @Override
          public void onOperationSuccess(BleDevice bleDevice) {
            L.i("onOperationSuccess1 "+" bleDevice:"+bleDevice);
          }

          @Override
          public void onFail(BleDevice bleDevice, int statuCode, String message) {
            L.i("onFail1 "+" bleDevice:"+bleDevice+" statuCode:"+statuCode+" message:"+message);
          }

          @Override
          public void onComplete(BleDevice bleDevice) {
            L.i("onComplete1 "+" bleDevice:"+bleDevice);
          }
        });

        if (mBleDevice!=null){
          mBleAdmin.addTask(enableTask);
        }

        if (mDevice1!=null){
          mBleAdmin.addTask(enableTask1);
        }
      }
    });
    mBinding.btnGetConnectedDevice.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        List<BleDevice> connectedDevice = mBleAdmin.getMultipleBleController().getConnectedDevice();
        L.v("connectedDevice:"+connectedDevice);
      }
    });

  }
}
