package cc.noharry.bledemo.ui;

import android.Manifest.permission;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;
import androidx.navigation.Navigation;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.databinding.ActivityHomeBinding;
import cc.noharry.bledemo.ui.toolbar.IWithBack;
import cc.noharry.bledemo.ui.toolbar.IWithoutBack;
import cc.noharry.bledemo.ui.view.LogDialog;
import cc.noharry.bledemo.ui.view.LogDialog.OnLogListener;
import cc.noharry.bledemo.util.PermissionUtils;
import cc.noharry.bledemo.util.PermissionUtils.OnPermissionListener;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import cc.noharry.bledemo.viewmodel.ViewModelFactory;

public class HomeActivity extends AppCompatActivity {
  private ActivityHomeBinding mBinding;
  private HomeViewmodel mHomeViewmodel;
  private LogDialog mDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCustomDensity(this,getApplication());
    mBinding=DataBindingUtil.setContentView(this,R.layout.activity_home);
    initFragmentLifeCycle();
    initData();
    initObserver();
    initFb();
    requestPermission();
  }

  private void checkLocation() {

      Toast.makeText(this, R.string.toast_open_location,
          Toast.LENGTH_SHORT).show();
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setMessage(getString(R.string.toast_open_location));
      dialog.setPositiveButton(getString(R.string.confirm),
          new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

              // 转到手机设置界面，用户设置GPS
              Intent intent = new Intent(
                  Settings.ACTION_LOCATION_SOURCE_SETTINGS);
              startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
            }
          });
      dialog.setNeutralButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
          arg0.dismiss();

        }
      });
      dialog.show();

  }

  private void requestPermission() {
    PermissionUtils.requestPermissions(this, 100, new String[]{permission.ACCESS_COARSE_LOCATION},
        new OnPermissionListener() {
          @Override
          public void onPermissionGranted() {

          }

          @Override
          public void onPermissionDenied() {

          }
        });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  static {
//设置VectorDrawable兼容支持，否则会闪退
    AppCompatDelegate
        .setCompatVectorFromResourcesEnabled(true);
  }

  private void initData() {
    mHomeViewmodel = obtainViewModel(this);
    mBinding.setViewmodel(mHomeViewmodel);
  }

  private void initObserver() {
    mHomeViewmodel.getLocationStatu().observe(this,(integer -> {
      switch (integer){
        case 0:
          break;
        case 1:
          checkLocation();
          break;
      }
    }));
    mHomeViewmodel.getHomeLogClean().observe(this,(integer -> {
      if (mDialog!=null){
        mDialog.notifyClean();
      }
    }));
    mHomeViewmodel.getLogSize().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(@Nullable Integer integer) {
        if (mDialog!=null){
          mDialog.notifyLog(integer.intValue());
        }
      }
    });

  }

  private void initFb() {
    mBinding.fab.setOnClickListener((v)-> showDialog());
  }

  public interface OnFabClickListener{
    void onFabClick();
  }

  private OnFabClickListener mFabClickListener=null;
  public void setOnFabClickListener(OnFabClickListener listener){
    mFabClickListener=listener;
  }

  private void showDialog() {
    mDialog = new LogDialog(this,this,mHomeViewmodel.getLogList());
//    mHomeViewmodel.displayLog();
    mDialog.setOnLogListener(new OnLogListener() {
      @Override
      public void onCleanLog() {
        mHomeViewmodel.clearLog();
      }
    });
    mDialog.show();
  }


  private static float sNoCompatDensity;
  private static float sNoCompatScaleDensity;
  public void setCustomDensity(Activity activity,Application application){
    final DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
    if (sNoCompatDensity==0){
      sNoCompatDensity=displayMetrics.density;
      sNoCompatScaleDensity=displayMetrics.scaledDensity;


      final float targetDensity=displayMetrics.widthPixels/420.0f;
//      final float targetDensity=displayMetrics.widthPixels/360.0f;
      final float targetScaleDensity=targetDensity*(sNoCompatScaleDensity/sNoCompatDensity);
      final int targetDensityDpi= (int) (160*targetDensity);


      displayMetrics.density=targetDensity;
      displayMetrics.scaledDensity=targetScaleDensity;
      displayMetrics.densityDpi=targetDensityDpi;

      final DisplayMetrics activityMetrics = activity.getResources().getDisplayMetrics();
      activityMetrics.density=targetDensity;
      activityMetrics.scaledDensity=targetScaleDensity;
      activityMetrics.densityDpi=targetDensityDpi;
    }
  }


  private void initFragmentLifeCycle() {
    setSupportActionBar(mBinding.includeToolbar.appToolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    FragmentManager.FragmentLifecycleCallbacks callbacks=new FragmentLifecycleCallbacks() {
      @Override
      public void onFragmentStarted(FragmentManager fm, Fragment f) {
        if (f instanceof IWithBack){
          actionBar.setDisplayHomeAsUpEnabled(true);
          actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24);
          mBinding.includeToolbar.appToolbar.setNavigationOnClickListener((v)-> onBackPressed());
        }else if (f instanceof IWithoutBack){
          actionBar.setDisplayHomeAsUpEnabled(false);
        }

      }
    };
    getSupportFragmentManager().registerFragmentLifecycleCallbacks(callbacks,true);
  }

  public TextView getToolbarTitle(){
    return mBinding.includeToolbar.toolbarTitle;
  }

  @Override
  public boolean onSupportNavigateUp() {
    return Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp();
  }

  public static HomeViewmodel obtainViewModel(FragmentActivity activity) {
    // Use a Factory to inject dependencies into the ViewModel
    ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

    return ViewModelProviders.of(activity, factory).get(HomeViewmodel.class);
  }
}
