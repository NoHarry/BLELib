package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.util.Log;

/**
 * @author NoHarry
 * @date 2018/06/06
 */
public class LogDialog extends Dialog {

  private LinearLayout mLl;
  private Context mContext;

  public LogDialog(@NonNull Context context) {
    super(context);
    mContext=context;
  }

  public LogDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    mContext=context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_log);
    mLl = findViewById(R.id.dialog_ll);
  }

  public void addLog(Log log){
    TextView textView=new TextView(mContext);
    textView.setText(log.getContent());
    switch (log.getLevel()){
      case ERROR:
        textView.setTextColor(mContext.getResources().getColor(R.color.red));
        break;
      case INFO:
        textView.setTextColor(mContext.getResources().getColor(R.color.green));
        break;
      case DEBUG:
        textView.setTextColor(mContext.getResources().getColor(R.color.blue));
        break;
      case WARN:
        textView.setTextColor(mContext.getResources().getColor(R.color.yellow));
        break;
    }


    mLl.addView(textView);
  }
}
