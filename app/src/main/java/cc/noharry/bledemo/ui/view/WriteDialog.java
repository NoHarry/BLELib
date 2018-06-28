package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.util.MethodUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author NoHarry
 * @date 2018/06/14
 */
public class WriteDialog extends Dialog {

  private int mType = 0;
  private Context mContext;
  private static final String [] WRITE_TYPE ={"Text","Byte Array"};
  private ArrayAdapter<String> adapter = null;
  private TextView mSend;
  private TextView mCancel;
  private TextInputEditText mContent;
  private Spinner mSpinner;
  private static final int TYPE_TEXT=0;
  private static final int TYPE_HEX=1;
  private static Pattern HEXSTR=Pattern.compile("[a-fA-F0-9]+");
  private TextInputLayout mContainer;
  private TextView mTitle;
  private AtomicBoolean isOk=new AtomicBoolean(true);


  public WriteDialog(@NonNull Context context) {
    super(context);
    mContext=context;
  }

  public WriteDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    mContext=context;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_write);
    mSpinner = findViewById(R.id.dialog_write_spinner);
    adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,WRITE_TYPE);
    //设置下拉列表风格
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpinner.setAdapter(adapter);

    mSend = findViewById(R.id.dialog_write_send);
    mCancel = findViewById(R.id.dialog_write_cancel);
    mContent = findViewById(R.id.dialog_write_content);
    mTitle = findViewById(R.id.dialog_write_content_title);
    mContainer = findViewById(R.id.dialog_write_content_container);
    Window win = getWindow();
    if (win != null) {
      WindowManager.LayoutParams lp = win.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      win.setAttributes(lp);
    }
    setCanceledOnTouchOutside(false);
    initEvent();
  }

  private void initEvent() {
    mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        L.i("position:"+position+" id:"+id);
        switch (position){
          case TYPE_TEXT:
            mType=TYPE_TEXT;
            mTitle.setVisibility(View.GONE);
            mContent.setHint("");
            break;
          case TYPE_HEX:
            mType=TYPE_HEX;
            mTitle.setVisibility(View.VISIBLE);
            mContent.setHint(mContext.getResources().getString(R.string.editext_hint_hex));
            break;

            default:
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mContent.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        switch (mType){
          case TYPE_HEX:
            if (!TextUtils.isEmpty(s)&&!isHexNum(s.toString())){
              mContainer.setErrorEnabled(true);
              mContainer.setError(mContext.getResources().getString(R.string.editext_hint_hex_error));
              isOk.set(false);
            }else {
              if (mContainer.getError()!=null){
                mContainer.setError(null);
                mContainer.setErrorEnabled(false);
              }
              isOk.set(true);
            }
            break;
          case TYPE_TEXT:
            break;
            default:
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    if (mListener!=null){
      mSend.setOnClickListener((v -> getInputData()));
      mCancel.setOnClickListener((v -> mListener.onCancelClick(this)));
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mContent!=null){
      mContent.setText("");
    }
  }

  private void getInputData(){
    String data = mContent.getText().toString().trim();
    switch (mType){

      case TYPE_HEX:
        if (isOk.get()&&(data.length()%2==0)){
          byte[] bytes = MethodUtils.hexStringToByteArray(data);
          mListener.onSendClick(this,bytes);
        }else {
          mContainer.setErrorEnabled(true);
          mContainer.setError(mContext.getResources().getString(R.string.editext_hint_hex_error));
          isOk.set(false);
        }
        break;
      case TYPE_TEXT:
        mListener.onSendClick(this,data.getBytes());
        break;
        default:
    }


  }

  private boolean isHexNum(String s){
    boolean matches = HEXSTR.matcher(s).matches();
//    L.i("matches:"+matches+" s:"+s);
    return matches;
  }

  private WriteDialogListener mListener=null;
  public void setOnWriteDialogListener(WriteDialogListener listener){
    mListener=listener;
  }
  public interface WriteDialogListener{
    void onCancelClick(Dialog dialog);
    void onSendClick(Dialog dialog,byte[] data);
  }

}
