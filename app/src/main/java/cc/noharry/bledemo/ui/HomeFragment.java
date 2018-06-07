package cc.noharry.bledemo.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.navigation.Navigation;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.data.Device;
import cc.noharry.bledemo.databinding.FragmentHomeBinding;
import cc.noharry.bledemo.ui.adapter.DeviceAdapter;
import cc.noharry.bledemo.ui.adapter.DeviceAdapter.OnConnectClickListener;
import cc.noharry.bledemo.ui.toolbar.IWithoutBack;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.util.ThreadPoolProxyFactory;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class HomeFragment extends Fragment implements IWithoutBack {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private FragmentHomeBinding mBinding;
  private HomeViewmodel mHomeViewmodel;
  private Map<String,Device> mDeviceMap=new HashMap<>();
  private DeviceAdapter mAdapter;
  private Handler mHandler=new Handler(Looper.getMainLooper());
  private List<Device> mDeviceList=new ArrayList<>();
  private AnimatedVectorDrawable mVectorDrawable;
  private AtomicBoolean isBleOpen=new AtomicBoolean(false);
  private AnimatedVectorDrawable mVectorDrawable1;


  public HomeFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment HomeFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static HomeFragment newInstance(String param1, String param2) {
    HomeFragment fragment = new HomeFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }

  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
    initView();
    initData();
    initObserver();

    return mBinding.getRoot();
  }

  private void initView() {
    mAdapter = new DeviceAdapter(getActivity(),mDeviceList);
    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
    mBinding.homeRv.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
    mBinding.homeRv.setLayoutManager(linearLayoutManager);
    mBinding.homeRv.setAdapter(mAdapter);
    mVectorDrawable = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.ic_bluetooth_animated);
    mVectorDrawable1 = (AnimatedVectorDrawable) getActivity()
        .getDrawable(R.drawable.ic_bluetooth_animated);

    mAdapter.setOnConnectBtnClickListener(new OnConnectClickListener() {
      @Override
      public void onConnectClick(int position, Device device) {
        mHomeViewmodel.connect(device);

      }

      @Override
      public void onDisconnectClick(int position, Device device) {
        mHomeViewmodel.disConnect(device);
      }

      @Override
      public void onDetailClick(int position, View view, Device device) {
        Navigation.findNavController(view)
            .navigate(R.id.action_homeFragment_to_detailFragment);
      }


    });
  }

  private void initObserver() {
    mHomeViewmodel.getFoundDevice().observe(this, new Observer<Device>() {
      @Override
      public void onChanged(@Nullable Device device) {
        ThreadPoolProxyFactory.getUpdateThreadPoolProxy().submit(new Runnable() {
          @Override
          public void run() {
            updateData(device);
          }
        });

//
      }
    });
  }

  private void updateData(Device device) {
    String key = device.getKey().get();
//    L.i("updateData:"+device);
//    L.i("mDeviceList:"+mDeviceList+" key:"+key);
    if (mDeviceList.isEmpty()){
      mHandler.post(new Runnable() {
        @Override
        public void run() {
          mDeviceList.add(device);
          mAdapter.notifyItemInserted(0);

        }
      });

    }else {
      if (!mDeviceList.contains(device)){
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            mDeviceList.add(device);
            mAdapter.notifyItemInserted(mDeviceList.size());
          }
        });

      }else if (mDeviceList.contains(device)){
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            int i = mDeviceList.indexOf(device);
            mDeviceList.set(i,device);
            mAdapter.notifyItemChanged(i);
          }
        });
      }

    }
  }

  private void initData() {
    mHomeViewmodel = HomeActivity.obtainViewModel(getActivity());
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home_menu,menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.menu_scan:
        L.i("menu click");
        clearData();
        mHomeViewmodel.scan();

        break;
      default:
    }
    return super.onOptionsItemSelected(item);
  }

  private void clearData() {
    int size = mDeviceList.size();
    mDeviceList.clear();
    mAdapter.notifyItemRangeRemoved(0,size);
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
