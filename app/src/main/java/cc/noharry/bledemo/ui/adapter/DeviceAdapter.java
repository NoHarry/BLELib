package cc.noharry.bledemo.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.databinding.ItemDeviceBinding;
import cc.noharry.bledemo.ui.adapter.DeviceAdapter.MyViewHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class DeviceAdapter extends RecyclerView.Adapter<MyViewHolder> {
  private Map<String,Device> mDeviceMap;
  private Context mContext;
  private List<Device> mDeviceList=new ArrayList<>();

  public DeviceAdapter(Map<String, Device> deviceMap, Context context) {
    mDeviceMap = deviceMap;
    mContext = context;
    mDeviceList.clear();
    List<String> key=new ArrayList<>(mDeviceMap.keySet());
    for (String s:key){
      mDeviceList.add(mDeviceMap.get(s));
    }
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemDeviceBinding binding=DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_device,parent,false);
    return new MyViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.binding.setDevice(mDeviceList.get(position));
    holder.binding.executePendingBindings();
  }


  @Override
  public int getItemCount() {
    return mDeviceList.size();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder{
    public ItemDeviceBinding binding;
    public MyViewHolder(ItemDeviceBinding binding) {
      super(binding.getRoot());
      this.binding=binding;

    }

    public ItemDeviceBinding getBinding() {
      return binding;
    }
  }
}
