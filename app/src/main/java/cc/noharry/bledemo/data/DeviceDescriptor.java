package cc.noharry.bledemo.data;

import android.bluetooth.BluetoothGattDescriptor;
import cc.noharry.bledemo.ui.adapter.DeviceDetailAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author NoHarry
 * @date 2018/06/12
 */
public class DeviceDescriptor implements MultiItemEntity {
  private BluetoothGattDescriptor mBluetoothGattDescriptor;

  public DeviceDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
  }

  public BluetoothGattDescriptor getBluetoothGattDescriptor() {
    return mBluetoothGattDescriptor;
  }

  public void setBluetoothGattDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
  }

  @Override
  public int getItemType() {
    return DeviceDetailAdapter.TYPE_DESCRIPTOR;
  }
}
