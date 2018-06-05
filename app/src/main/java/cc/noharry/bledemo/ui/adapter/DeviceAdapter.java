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
import cc.noharry.bledemo.util.L;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class DeviceAdapter extends RecyclerView.Adapter<MyViewHolder> {
  private Context mContext;
  private List<Device> mDeviceList;



  public DeviceAdapter(Context context, List<Device> deviceList) {
    mContext = context;
    mDeviceList = deviceList;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemDeviceBinding binding=DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_device,parent,false);
    return new MyViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//    mKeyList=new ArrayList<>(mDeviceMap.keySet());

//    holder.binding.setDevice(mDeviceMap.get(mKeyList.get(position)));
    holder.binding.setDevice(mDeviceList.get(position));
    holder.binding.executePendingBindings();
  }


  @Override
  public int getItemCount() {
    return mDeviceList.size();
  }

  public void notifyDataChange(Device device){
    String key = device.getKey().get();

    for (int i=0;i<mDeviceList.size();i++){
      boolean checkUpdate = key != null && (key
          .equals(mDeviceList.get(i).getKey().get()));
      L.i("checkUpdate"+checkUpdate);
      if (checkUpdate){
        L.i("更新:"+device);
        notifyItemChanged(i);
      }
    }
  }

  public void notifyDataInsert(Device device){
    String key = device.getKey().get();

    for (int i=0;i<mDeviceList.size();i++){
      boolean checkNew = key != null && (key
          .equals(mDeviceList.get(i).getKey().get()));
      L.i("checkNew"+checkNew);
      if (checkNew){
        L.i("添加："+device);
        notifyItemInserted(i);
      }
    }
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
