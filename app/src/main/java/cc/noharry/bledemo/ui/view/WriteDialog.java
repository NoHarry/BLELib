package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import cc.noharry.bledemo.R;

/**
 * @author NoHarry
 * @date 2018/06/14
 */
public class WriteDialog extends Dialog {

  private int mType;
  private Context mContext;
  private static final String [] langurage ={"Text","Byte Array"};
  private ArrayAdapter<String> adapter = null;

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
    Spinner spinner=findViewById(R.id.dialog_write_spinner);
    adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,langurage);
    //设置下拉列表风格
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);


    Window win = getWindow();
    if (win != null) {
//      win.getDecorView().setPadding(0, 0, 0, 0);
      WindowManager.LayoutParams lp = win.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      win.setAttributes(lp);

    }
  }
}
