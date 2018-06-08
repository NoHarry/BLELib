package cc.noharry.bledemo.ui;


import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.databinding.FragmentDetailBinding;
import cc.noharry.bledemo.ui.toolbar.IWithBack;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment implements IWithBack {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private FragmentDetailBinding mBinding;
  private HomeViewmodel mHomeViewmodel;
  private Device mDevice;

  public DetailFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment DetailFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static DetailFragment newInstance(String param1, String param2) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false);
    initData();
    initObserver();
    return mBinding.getRoot();
  }

  private void initObserver() {
    mHomeViewmodel.getCurrentDevice().observe(this, new Observer<Device>() {
      @Override
      public void onChanged(@Nullable Device device) {
        L.i("DetailFragment:"+device);
        mDevice = device;
        BluetoothGatt bluetoothGatt = device.getGatt().get();
        if (bluetoothGatt!=null){
          for (BluetoothGattService service:bluetoothGatt.getServices()){
            L.i("BluetoothGattService:"+service.getUuid().toString());
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
              int properties = characteristic.getProperties();
              L.i("characteristic:"+characteristic.getUuid().toString()+" properties:"+getProperties(properties));
            }
          }

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

  private void initData() {
    mHomeViewmodel = HomeActivity.obtainViewModel(getActivity());
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

  }

  @Override
  public void onDetach() {
    super.onDetach();
  }


}
