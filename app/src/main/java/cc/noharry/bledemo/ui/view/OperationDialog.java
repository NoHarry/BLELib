package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.NonNull;
import cc.noharry.bledemo.data.Device;

/**
 * @author NoHarry
 * @date 2018/06/19
 */
public class OperationDialog extends Dialog {
  private Device mDevice;
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  private Context mContext;

  public OperationDialog(@NonNull Context context,
      Device device, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    super(context);
    mDevice = device;
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mContext=context;
  }

  public OperationDialog(@NonNull Context context, Device device,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    super(context);
    mDevice = device;
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    mContext=context;
  }



}
