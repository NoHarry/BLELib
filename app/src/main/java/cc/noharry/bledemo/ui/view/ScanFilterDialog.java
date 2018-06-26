package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import cc.noharry.bledemo.R;

/**
 * @author NoHarry
 * @date 2018/06/26
 */
public class ScanFilterDialog extends Dialog {
  private Context mContext;
  private EditText mMac;
  private EditText mUuid;
  private EditText mName;
  private TextView mConfirm;

  public ScanFilterDialog(@NonNull Context context) {
    super(context);
    mContext=context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_filter);
    initView();
    initEvent();
    Window win = getWindow();
    if (win != null) {
      WindowManager.LayoutParams lp = win.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      win.setAttributes(lp);
    }
  }

  private void initEvent() {
    mConfirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        handleData();
        dismiss();
      }
    });
  }

  private void handleData() {
    String mac=null;
    String name=null;
    String uuid=null;
    String et_mac = mMac.getText().toString().trim();
    String et_uuid = mUuid.getText().toString().trim();
    String et_name = mName.getText().toString().trim();
    if (!TextUtils.isEmpty(et_mac)){
      mac=et_mac;
    }
    if (!TextUtils.isEmpty(et_uuid)){
      uuid=et_uuid;
    }
    if (!TextUtils.isEmpty(et_name)){
      name=et_name;
    }
    mOnFilterConfirmListener.onConfirm(mac,uuid,name);
  }

  private void initView() {
    mMac = findViewById(R.id.dialog_filter_mac);
    mUuid = findViewById(R.id.dialog_filter_uuid);
    mName = findViewById(R.id.dialog_filter_name);
    mConfirm = findViewById(R.id.dialog_filter_confirm);
    setCanceledOnTouchOutside(false);
  }

  public interface OnFilterConfirmListener{
    void onConfirm(String mac,String uuid,String name);
  }
  private OnFilterConfirmListener mOnFilterConfirmListener=null;

  public void setOnFilterConfirmListener(
      OnFilterConfirmListener onFilterConfirmListener) {
    mOnFilterConfirmListener = onFilterConfirmListener;
  }
}
