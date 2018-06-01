package cc.noharry.bledemo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import androidx.navigation.Navigation;
import cc.noharry.bledemo.R;
import cc.noharry.bledemo.databinding.ActivityHomeBinding;
import cc.noharry.bledemo.ui.toolbar.IWithBack;
import cc.noharry.bledemo.ui.toolbar.IWithoutBack;
import cc.noharry.bledemo.viewmodel.HomeViewmodel;
import cc.noharry.bledemo.viewmodel.ViewModelFactory;

public class HomeActivity extends AppCompatActivity {
  private ActivityHomeBinding mBinding;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding=DataBindingUtil.setContentView(this,R.layout.activity_home);
    initFragmentLifeCycle();
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
