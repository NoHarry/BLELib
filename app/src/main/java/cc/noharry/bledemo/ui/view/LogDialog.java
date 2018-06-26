package cc.noharry.bledemo.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.databinding.DialogLogBinding;
import cc.noharry.bledemo.ui.adapter.LogAdapter;
import cc.noharry.bledemo.util.L;
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
  private TextView mClear;
  private TextView mClose;


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
    initView();
    initEvent();
    initRV();
    Window win = getWindow();
    if (win != null) {
      WindowManager.LayoutParams lp = win.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      win.setAttributes(lp);
    }
    setCanceledOnTouchOutside(false);
  }

  private void initEvent() {
    mClear.setOnClickListener((v -> {
      mOnLogListener.onCleanLog();
    }));
    mClose.setOnClickListener((v -> {
      dismiss();
    }));
  }

  private void initView() {
    mRecyclerView = findViewById(R.id.dialog_rv);
    mClear = findViewById(R.id.dialog_clear_log);
    mClose = findViewById(R.id.dialog_close);
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

  public void addLog(Log log){

    mLogList.add(log);
    mAdapter.notifyItemInserted(mLogList.size());
    mRecyclerView.scrollToPosition(mLogList.size());
  }

  public void notifyLog(){
    mAdapter.notifyItemInserted(mLogList.size());
    mRecyclerView.scrollToPosition(mLogList.size());
  }

  public void notifyLog(int position){
    mAdapter.notifyItemInserted(position);
    mRecyclerView.scrollToPosition(position);
  }

  private void handleClean() {
    if (mOnLogListener!=null){
      mOnLogListener.onCleanLog();
    }
  }

  public void notifyClean(){
    mLogList.clear();
    mAdapter.notifyDataSetChanged();
  }

  public interface OnLogListener{
    void onCleanLog();
  }

  private OnLogListener mOnLogListener=null;

  public void setOnLogListener(OnLogListener listener){
    mOnLogListener=listener;
  }
}
