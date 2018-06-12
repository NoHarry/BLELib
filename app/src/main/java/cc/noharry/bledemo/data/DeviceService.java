package cc.noharry.bledemo.data;

import cc.noharry.bledemo.ui.adapter.DeviceDetailAdapter;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author NoHarry
 * @date 2018/06/12
 */
public class DeviceService extends AbstractExpandableItem<DeviceCharacteristic> implements
    MultiItemEntity {

  @Override
  public int getLevel() {
    return DeviceDetailAdapter.TYPE_SERVICE;
  }

  @Override
  public int getItemType() {
    return DeviceDetailAdapter.TYPE_SERVICE;
  }
}
