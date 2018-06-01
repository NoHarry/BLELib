package cc.noharry.bledemo.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import cc.noharry.bledemo.ui.toolbar.IWithoutBack;
import cc.noharry.bledemo.util.L;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import java.util.HashMap;
import java.util.Map;


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

  private void initEvent() {
    mBinding.btTest.setOnClickListener(
        (v)->Navigation.findNavController(v)
            .navigate(R.id.action_homeFragment_to_detailFragment));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
    initData();
    initEvent();
    initObserver();
    initView();
    return mBinding.getRoot();
  }

  private void initView() {
    mAdapter = new DeviceAdapter(mDeviceMap,getActivity());
    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
    mBinding.homeRv.setLayoutManager(linearLayoutManager);
    mBinding.homeRv.setAdapter(mAdapter);
  }

  private void initObserver() {
    mHomeViewmodel.getFoundDevice().observe(this, new Observer<Device>() {
      @Override
      public void onChanged(@Nullable Device device) {
        /*if (mDeviceMap.containsKey(device.getKey())){

        }*/
        L.i("observe");
        mDeviceMap.put(device.getKey().get(),device);
        mAdapter.notifyDataSetChanged();
      }
    });
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
        mHomeViewmodel.scan();
        break;
    }
    return super.onOptionsItemSelected(item);
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
