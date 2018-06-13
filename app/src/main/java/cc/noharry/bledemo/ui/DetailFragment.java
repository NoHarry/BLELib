package cc.noharry.bledemo.ui;


import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.data.DeviceCharacteristic;
import cc.noharry.bledemo.data.DeviceDescriptor;
import cc.noharry.bledemo.data.DeviceService;
import cc.noharry.bledemo.databinding.FragmentDetailBinding;
import cc.noharry.bledemo.ui.adapter.DeviceDetailAdapter;
import cc.noharry.bledemo.ui.adapter.DeviceDetailAdapter.OnCharacteristicClickListener;
import cc.noharry.bledemo.ui.toolbar.IWithBack;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


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
  private List<MultiItemEntity> mList=new ArrayList<>();
  private DeviceDetailAdapter mAdapter;
  private MenuItem mItem;
  private AtomicBoolean isConnect=new AtomicBoolean(false);

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
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false);
    initView();
    initData();
    initObserver();

    return mBinding.getRoot();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.detail_menu,menu);
    mItem = menu.findItem(R.id.menu_connect);
    if (mDevice!=null){
      if (mDevice.getState().get()==Device.CONNECTED){
        mItem.setTitle(getString(R.string.menu_disconnect));
      }else {
        mItem.setTitle(getString(R.string.menu_connect));
      }
    }
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.menu_connect:
        if (isConnect.get()){
          mHomeViewmodel.disConnect(mDevice);
        }else {
          mHomeViewmodel.connect(mDevice);
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void initView() {
    LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity()
        ,LinearLayoutManager.VERTICAL,false);
    mAdapter = new DeviceDetailAdapter(mList);
    mBinding.rvDetail.setAdapter(mAdapter);
    mBinding.rvDetail.setLayoutManager(layoutManager);
    mBinding.rvDetail.addItemDecoration(
        new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
    mAdapter.setCharacteristicClickListener(new OnCharacteristicClickListener() {
      @Override
      public void onRead(BluetoothGattCharacteristic characteristic, View view, int position) {
        mHomeViewmodel.read(mDevice,characteristic);
//        mAdapter.notifyItemChanged(position);
//        mAdapter.notifyDataSetChanged();
      }

      @Override
      public void onWrite(BluetoothGattCharacteristic characteristic, View view, int position) {
        mHomeViewmodel.write(mDevice,characteristic,"123".getBytes());
      }
    });
  }

  private void initObserver() {
    mHomeViewmodel.getCurrentDevice().observe(this, new Observer<Device>() {
      @Override
      public void onChanged(@Nullable Device device) {
        L.i("DetailFragment:"+device);
        mDevice = device;
        BluetoothGatt bluetoothGatt = device.getGatt().get();
        if (mDevice.getState().get()==Device.CONNECTED){
          mAdapter.isConnected.set(true);
          mList.clear();
          isConnect.set(true);
          if (mItem!=null){
            mItem.setTitle(getString(R.string.menu_disconnect));
          }

        }else {
          mAdapter.isConnected.set(false);
          isConnect.set(false);
          if (mItem!=null){
            mItem.setTitle(getString(R.string.menu_connect));
          }

        }
        if (bluetoothGatt!=null){
          for (BluetoothGattService service:bluetoothGatt.getServices()){
            L.i("BluetoothGattService:"+service.getUuid().toString());
            DeviceService deviceService=new DeviceService(service);
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
              int properties = characteristic.getProperties();
              DeviceCharacteristic deviceCharacteristic=new DeviceCharacteristic(characteristic);
              deviceService.addSubItem(deviceCharacteristic);
              L.i("characteristic:"+characteristic.getUuid().toString()+" properties:"+getProperties(properties));
              for (BluetoothGattDescriptor descriptor:characteristic.getDescriptors()){
                DeviceDescriptor deviceDescriptor=new DeviceDescriptor(descriptor);
                deviceCharacteristic.addSubItem(deviceDescriptor);
                L.i("descriptor:"+descriptor.getUuid()+" per:"+descriptor.getPermissions());
              }
            }
            mList.add(deviceService);
          }

        }
        if (mAdapter!=null){
          mAdapter.notifyDataSetChanged();
        }
      }
    });

    mHomeViewmodel.getValueChange().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(@Nullable Integer integer) {
        if (mAdapter!=null){
          mAdapter.notifyDataSetChanged();
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
