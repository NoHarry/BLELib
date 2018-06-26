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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.ui.adapter.LogAdapter;
import cc.noharry.bledemo.ui.view.WriteDialog.WriteDialogListener;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.util.Log;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/19
 */
public class OperationDialog extends Dialog implements OnClickListener {
  private Device mDevice;
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  private Context mContext;
  private List<Log> mLogList;
  private LogAdapter mAdapter;
  private RecyclerView mRecyclerView;
  private ImageView mWrite;
  private ImageView mRead;
  private ToggleImageView mNotify;
  private static final int TYPE_CHARACTERISTIC=0;
  private static final int TYPE_DESCRIPTOR=1;
  private int type=0;
  private ImageView mCleanLog;

  public OperationDialog(@NonNull Context context,
      Device device, BluetoothGattCharacteristic bluetoothGattCharacteristic,List<Log> logList) {
    super(context);
    mDevice = device;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mContext=context;
    mLogList=logList;
    type=TYPE_CHARACTERISTIC;
  }

  public OperationDialog(@NonNull Context context, Device device,
      BluetoothGattDescriptor bluetoothGattDescriptor,List<Log> logList) {
    super(context);
    mDevice = device;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    mContext=context;
    mLogList=logList;
    type=TYPE_DESCRIPTOR;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_operation);
    initView();
    initEvent();
    initRV();
    Window win = getWindow();
    if (win != null) {
      WindowManager.LayoutParams lp = win.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      win.setAttributes(lp);
    }
  }

  private void initEvent() {
    mWrite.setOnClickListener(this);
    mRead.setOnClickListener(this);
    mNotify.setOnClickListener(this);
    mCleanLog.setOnClickListener(this);
  }

  private void initView() {
    mRecyclerView=findViewById(R.id.rv_dialog_operation);
    mWrite = findViewById(R.id.iv_characteristic_write);
    mRead = findViewById(R.id.iv_characteristic_read);
    mNotify = findViewById(R.id.iv_characteristic_notify);
    mCleanLog = findViewById(R.id.iv_characteristic_clean);
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
  public void notifyLog(){
    mAdapter.notifyItemInserted(mLogList.size());
    mRecyclerView.scrollToPosition(mLogList.size());
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.iv_characteristic_write:
        handleWrite();
        break;
      case R.id.iv_characteristic_read:
        handleRead();
        break;
      case R.id.iv_characteristic_notify:
        handleNotify();
        break;
      case R.id.iv_characteristic_clean:
        handleClean();
        break;


    }
  }

  private void handleClean() {
    if (mOnOperationListener!=null){
      mOnOperationListener.onCleanLog();
    }
  }

  public void notifyClean(){
    mLogList.clear();
    mAdapter.notifyDataSetChanged();
  }
  private void handleNotify() {
    if (mOnOperationListener!=null){
      if (mNotify.getIsSwitchOn()){
        mNotify.setSwitchMode(ToggleImageView.SWITCH_MODE_OFF).doSwitch();
        mOnOperationListener.onDisableNotify(mDevice,mBluetoothGattCharacteristic);
      }else {
        mNotify.setSwitchMode(ToggleImageView.SWITCH_MODE_ON).doSwitch();
        mOnOperationListener.onEnableNotify(mDevice,mBluetoothGattCharacteristic);
      }
    }
  }

  private void handleRead() {
    if (mOnOperationListener!=null){
      if (type==0){
        mOnOperationListener.onCharacteristicRead(mDevice,mBluetoothGattCharacteristic);
      }else {
        mOnOperationListener.onDescriptorRead(mDevice,mBluetoothGattDescriptor);
      }
    }

  }

  private void handleWrite() {
    WriteDialog dialog=new WriteDialog(getContext());
    dialog.setOnWriteDialogListener(new WriteDialogListener() {
      @Override
      public void onCancelClick(Dialog dialog) {
        dialog.dismiss();
      }

      @Override
      public void onSendClick(Dialog dialog, byte[] data) {
        if (mOnOperationListener!=null){
          if (type==0){
            mOnOperationListener.onCharacteristicWrite(mDevice,mBluetoothGattCharacteristic,data);
          }else {
            mOnOperationListener.onDescriptorWrite(mDevice,mBluetoothGattDescriptor,data);
          }

        }
        dialog.dismiss();
      }
    });
    dialog.show();
  }

  public interface OnOperationListener{
    void onCharacteristicWrite(Device device
        , BluetoothGattCharacteristic bluetoothGattCharacteristic,byte[] data);
    void onDescriptorWrite(Device device
        , BluetoothGattDescriptor bluetoothGattDescriptor,byte[] data);
    void onCharacteristicRead(Device device
        , BluetoothGattCharacteristic bluetoothGattCharacteristic);
    void onDescriptorRead(Device device,
        BluetoothGattDescriptor bluetoothGattDescriptor);
    void onEnableNotify(Device device
        , BluetoothGattCharacteristic bluetoothGattCharacteristic);
    void onDisableNotify(Device device
        , BluetoothGattCharacteristic bluetoothGattCharacteristic);
    void onCleanLog();
  }

  private OnOperationListener mOnOperationListener=null;

  public void setOnOperationListener(OnOperationListener listener){
    mOnOperationListener=listener;
  }

}
