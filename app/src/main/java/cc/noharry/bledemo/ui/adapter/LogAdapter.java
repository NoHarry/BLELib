package cc.noharry.bledemo.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.databinding.ItemLogBinding;
import cc.noharry.bledemo.ui.adapter.LogAdapter.MyViewHolder;
import cc.noharry.bledemo.util.Log;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/11
 */
public class LogAdapter extends RecyclerView.Adapter<MyViewHolder> {
  private Context mContext;
  private List<Log> mLogList;

  public LogAdapter(Context context, List<Log> logList) {
    mContext = context;
    mLogList = logList;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemLogBinding itemLogBinding=DataBindingUtil
        .inflate(LayoutInflater.from(mContext)
            , R.layout.item_log,parent,false);
    MyViewHolder viewHolder=new MyViewHolder(itemLogBinding);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    Log log = mLogList.get(position);
    switch (log.getLevel()){
      case ERROR:
        holder.mBinding.itemTvLog.setTextColor(mContext.getResources().getColor(R.color.red));
        break;
      case INFO:
        holder.mBinding.itemTvLog.setTextColor(mContext.getResources().getColor(R.color.green));
        break;
      case DEBUG:
        holder.mBinding.itemTvLog.setTextColor(mContext.getResources().getColor(R.color.blue));
        break;
      case WARN:
        holder.mBinding.itemTvLog.setTextColor(mContext.getResources().getColor(R.color.yellow));
        break;
    }
    holder.mBinding.setLog(mLogList.get(position));
    holder.mBinding.executePendingBindings();
  }


  @Override
  public int getItemCount() {
    return mLogList.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder{
    public ItemLogBinding mBinding;

    public MyViewHolder(ItemLogBinding binding) {
      super(binding.getRoot());
      mBinding=binding;
    }
  }
}
