package cc.noharry.bledemo.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.databinding.DialogLogBinding;
import cc.noharry.bledemo.ui.adapter.LogAdapter;
import cc.noharry.bledemo.util.Log;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/06
 */
public class LogDialog extends Dialog {

//  private LinearLayout mLl;
  private Context mContext;
  private Activity mActivity;
  private List<Log> mLogList;
  private DialogLogBinding mBinding;
  private LogAdapter mAdapter;
  private RecyclerView mRecyclerView;

  public LogDialog(@NonNull Context context, Activity activity,
      List<Log> logList) {
    super(context);
    mContext=context;
    mActivity = activity;
    mLogList = logList;
  }

  public LogDialog(@NonNull Context context, int themeResId, Activity activity,
      List<Log> logList) {
    super(context, themeResId);
    mContext=context;
    mActivity = activity;
    mLogList = logList;
  }

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
    mRecyclerView = findViewById(R.id.dialog_rv);
    initRV();
  }

  private void initRV() {
    mAdapter = new LogAdapter(mContext,mLogList);
    LayoutManager layoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(layoutManager);
  }

  public void addLog(Log log){
   /* TextView textView=new TextView(mContext);
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
    }*/

    mLogList.add(log);
    mAdapter.notifyItemInserted(mLogList.size());
    mRecyclerView.scrollToPosition(mLogList.size());
//    mAdapter.notifyDataSetChanged();
//    mLl.addView(textView);
  }
}
