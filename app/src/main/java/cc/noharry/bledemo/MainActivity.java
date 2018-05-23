package cc.noharry.bledemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import cc.noharry.blelib.ble.BLEAdmin;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.callback.BleScanCallback;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.util.L;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

  private Handler mHandler=new Handler();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.btn_scan).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //0000ffe0-0000-1000-8000-00805f9b34fb
        UUID[] uuids=new UUID[]{UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"),UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")};
        BleScanConfig config=new BleScanConfig.Builder()
            .setUUID(uuids)
            .setScanTime(5000)
            .setDeviceName(new String[]{"LL0835","android"},true)
//            .setDeviceMac(new String[]{"00:0E:0B:14:70:DE","24:0A:C4:10:A2:22"})
            .build();

        BLEAdmin.getINSTANCE(getApplication()).scan(config, new BleScanCallback() {
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
          }
        });

      }
    });
    findViewById(R.id.btn_stop_scan).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        BLEAdmin.getINSTANCE(getApplicationContext()).stopScan();
      }
    });
  }
}
