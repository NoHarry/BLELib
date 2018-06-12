package cc.noharry.bledemo.ui.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.List;

/**
 * @author NoHarry
 * @date 2018/06/12
 */
public class DeviceDetailAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {
  public static final int TYPE_SERVICE=0;
  public static final int TYPE_CHARACTERISTIC=1;
  public static final int TYPE_DESCRIPTOR=2;


  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with
   * some initialization data.
   *
   * @param data A new list is created out of this one to avoid mutable list
   */
  public DeviceDetailAdapter(
      List<MultiItemEntity> data) {
    super(data);
  }

  @Override
  protected void convert(BaseViewHolder helper, MultiItemEntity item) {

  }
}
