package cc.noharry.bledemo.app;

import android.app.Application;
import cc.noharry.bledemo.util.L;

/**
 * @author NoHarry
 * @date 2018/06/01
 */
public class MyApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
//    initBugly();
  }

  @Override
  public void onTerminate() {
    L.e("onTerminate1");
    super.onTerminate();
    L.e("onTerminate");
  }

  /*private void initBugly() {
    CrashReport.initCrashReport(getApplicationContext(), "c6f6f76bc7", true);
  }*/
}
