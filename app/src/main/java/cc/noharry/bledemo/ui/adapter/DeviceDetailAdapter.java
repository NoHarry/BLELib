package cc.noharry.bledemo.ui.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.DeviceCharacteristic;
import cc.noharry.bledemo.data.DeviceDescriptor;
import cc.noharry.bledemo.data.DeviceService;
import cc.noharry.bledemo.util.MethodUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/06/12
 */
public class DeviceDetailAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {
  public static final int TYPE_SERVICE=0;
  public static final int TYPE_CHARACTERISTIC=1;
  public static final int TYPE_DESCRIPTOR=2;
  public AtomicBoolean isConnected=new AtomicBoolean(false);


  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with
   * some initialization data.
   *
   * @param data A new list is created out of this one to avoid mutable list
   */
  public DeviceDetailAdapter(
      List<MultiItemEntity> data) {
    super(data);
    addItemType(TYPE_SERVICE, R.layout.item_service);
    addItemType(TYPE_CHARACTERISTIC,R.layout.item_characteristic);
    addItemType(TYPE_DESCRIPTOR,R.layout.item_descriptor);
  }

  @Override
  protected void convert(BaseViewHolder helper, MultiItemEntity item) {
    switch (item.getItemType()){
      case TYPE_SERVICE:
        handleService(helper, (DeviceService) item);
        break;
      case TYPE_CHARACTERISTIC:
        handleCharacteristic(helper, (DeviceCharacteristic) item);
        break;
      case TYPE_DESCRIPTOR:
        handleDescriptor(helper, (DeviceDescriptor) item);
        break;
    }
  }

  private void handleDescriptor(BaseViewHolder helper, DeviceDescriptor item) {
    DeviceDescriptor descriptor= item;
    if (isConnected.get()){
      helper.setText(R.id.tv_descriptor_uuid,descriptor.getBluetoothGattDescriptor().getUuid().toString());
//      L.i("descriptor:"+ Arrays.toString(descriptor.getBluetoothGattDescriptor().getValue()));

      helper.itemView.setAlpha(1);
    }else {
//      helper.setText(R.id.tv_descriptor_uuid,descriptor.getBluetoothGattDescriptor().getUuid().toString());
      setToolGone(helper);
      helper.itemView.setAlpha(0.5f);
    }

  }

  private void handleCharacteristic(BaseViewHolder helper, DeviceCharacteristic item) {
    DeviceCharacteristic characteristic= item;
    if (isConnected.get()){

      helper.setText(R.id.tv_characteristic_uuid,characteristic.getBluetoothGattCharacteristic().getUuid().toString());
      helper.setText(R.id.tv_characteristic_properties
          ,getProperties(characteristic
              .getBluetoothGattCharacteristic().getProperties()).toString());
      helper.setText(R.id.tv_characteristic_value
          ,MethodUtils.getHexString(characteristic
              .getBluetoothGattCharacteristic().getValue()));
//      L.i("characteristic:"+Arrays.toString(characteristic.getBluetoothGattCharacteristic().getValue()));
      setToolVisiable(helper,characteristic
          .getBluetoothGattCharacteristic().getProperties());
      helper.itemView.setAlpha(1);
    }else {

      /*helper.setText(R.id.tv_characteristic_uuid,characteristic.getBluetoothGattCharacteristic().getUuid().toString());
      helper.setText(R.id.tv_characteristic_properties
          ,getProperties(characteristic
              .getBluetoothGattCharacteristic().getProperties()).toString());*/
      setToolGone(helper);
      helper.itemView.setAlpha(0.5f);

    }
    helper.itemView.findViewById(R.id.iv_characteristic_read).setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCharacteristicClickListener.onRead(characteristic
                .getBluetoothGattCharacteristic(),helper.itemView,helper.getAdapterPosition());
          }
        });
    helper.itemView.findViewById(R.id.iv_characteristic_write).setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCharacteristicClickListener.onWrite(characteristic
                .getBluetoothGattCharacteristic(),helper.itemView,helper.getAdapterPosition());
          }
        });


    helper.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int pos = helper.getAdapterPosition();
        if (!characteristic.isExpanded()){
          expand(pos, false);
        }
        /*if (characteristic.isExpanded()) {
          collapse(pos, false);
        } else {
          expand(pos, false);
        }*/
      }
    });
  }

  private void handleService(BaseViewHolder helper, DeviceService item) {
    DeviceService service= item;
    if (isConnected.get()){

      helper.setText(R.id.tv_service_uuid,service.getBluetoothGattService().getUuid().toString());
      helper.itemView.setAlpha(1);
    }else {

//      helper.setText(R.id.tv_service_uuid,service.getBluetoothGattService().getUuid().toString());
      helper.itemView.setAlpha(0.5f);
    }
    helper.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int pos = helper.getAdapterPosition();
        if (service.isExpanded()) {
          collapse(pos, false);
        } else {
          expand(pos, false);
        }
      }
    });
  }

  private List<String> getProperties(int properties){
    List<String> result=new ArrayList<>();
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_NOTIFY))!=0){
      result.add("NOTIFY");
    }
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_INDICATE))!=0){
      result.add("INDICATE");
    }
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_READ))!=0){
      result.add("READ");
    }
    if ((properties&(BluetoothGattCharacteristic.PROPERTY_WRITE))!=0){
      result.add("WRITE");
    }

    return result;

  }

  private void setToolVisiable(BaseViewHolder helper,int properties){
    List<String> list = getProperties(properties);
    ImageView write=helper.itemView.findViewById(R.id.iv_characteristic_write);
    ImageView read=helper.itemView.findViewById(R.id.iv_characteristic_read);
    if (list.contains("READ")){
      read.setVisibility(View.VISIBLE);
    }else {
      read.setVisibility(View.GONE);
    }

    if (list.contains("WRITE")){
      write.setVisibility(View.VISIBLE);
    }else {
      write.setVisibility(View.GONE);
    }
  }

  private void setToolGone(BaseViewHolder helper){
    ImageView write=helper.itemView.findViewById(R.id.iv_characteristic_write);
    ImageView read=helper.itemView.findViewById(R.id.iv_characteristic_read);
    if (write!=null&&read!=null){
      write.setVisibility(View.GONE);
      read.setVisibility(View.GONE);
    }

  }


  public interface OnCharacteristicClickListener{
    void onRead(BluetoothGattCharacteristic characteristic,View view,int position);
    void onWrite(BluetoothGattCharacteristic characteristic,View view,int position);
  }

  private OnCharacteristicClickListener mCharacteristicClickListener=null;
  public void setCharacteristicClickListener(OnCharacteristicClickListener listener){
    mCharacteristicClickListener=listener;
  }

}
