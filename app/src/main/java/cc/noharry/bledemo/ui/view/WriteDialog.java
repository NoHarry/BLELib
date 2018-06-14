package cc.noharry.bledemo.ui.view;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author NoHarry
 * @date 2018/06/14
 */
public class WriteDialog extends Dialog {
  private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
  private BluetoothGattDescriptor mBluetoothGattDescriptor;
  public static final int TYPE_CHARACTERISTIC=0;
  public static final int TYPE_DESCRIPTOR=1;
  private int mType;
  private Context mContext;

  public WriteDialog(@NonNull Context context,
      BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    super(context);
    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    mContext=context;
    mType=TYPE_CHARACTERISTIC;
  }

  public WriteDialog(@NonNull Context context,
      BluetoothGattDescriptor bluetoothGattDescriptor) {
    super(context);
    mBluetoothGattDescriptor = bluetoothGattDescriptor;
    mContext=context;
    mType=TYPE_DESCRIPTOR;
  }

  public WriteDialog(@NonNull Context context) {
    super(context);
  }

  public WriteDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
  }
}
