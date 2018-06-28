package cc.noharry.bledemo.app;

import android.app.Application;

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

  /*private void initBugly() {
    CrashReport.initCrashReport(getApplicationContext(), "c6f6f76bc7", true);
  }*/
}
