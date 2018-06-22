package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.ui.adapter.LogAdapter;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.util.Log;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/19
 */
public class OperationDialog extends Dialog {
  private Device mDevice;
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  private Context mContext;
  private List<Log> mLogList;
  private LogAdapter mAdapter;
  private RecyclerView mRecyclerView;

  public OperationDialog(@NonNull Context context,
      Device device, BluetoothGattCharacteristic bluetoothGattCharacteristic,List<Log> logList) {
    super(context);
    mDevice = device;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mContext=context;
    mLogList=logList;
  }

  public OperationDialog(@NonNull Context context, Device device,
      BluetoothGattDescriptor bluetoothGattDescriptor,List<Log> logList) {
    super(context);
    mDevice = device;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    mContext=context;
    mLogList=logList;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_operation);
    mRecyclerView=findViewById(R.id.rv_dialog_operation);
    initRV();
  }

  private void initRV() {
    mAdapter = new LogAdapter(mContext,mLogList);
    LayoutManager layoutManager=new LinearLayoutManager(mContext
        ,LinearLayoutManager.VERTICAL
        ,false);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(layoutManager);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mRecyclerView!=null){
      mRecyclerView.scrollToPosition(mLogList.size());
    }
    L.i("");
  }

  public void notifyLog(int position){
    mAdapter.notifyItemInserted(position);
    mRecyclerView.scrollToPosition(position);
  }

}
