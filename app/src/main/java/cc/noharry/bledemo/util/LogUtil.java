package cc.noharry.bledemo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author NoHarry
 * @date 2018/06/06
 */

public class LogUtil {
  public enum Level{
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
  }
  public interface logCallback{
    void onLog(Log log);
    }
  private logCallback mLogCallback=null;

  public static void readLog(final logCallback callback)  {
//    Log.i("INFO", "start connectLog");

    new Thread(new Runnable() {
      @Override
      public void run() {
        int pid=android.os.Process.myPid();
        StringBuffer sb = new StringBuffer();
        ArrayList<String> cmdLine = new ArrayList<String>();
        cmdLine.add("logcat");
//        cmdLine.add("logcat -d -v time *:D | tail -n 1000");
//    cmdLine.add("-c");
        cmdLine.add("-v");
        cmdLine.add("time");
//    cmdLine.add("-d");//收集一次日志停止
//        cmdLine.add("-s");//过滤
        cmdLine.add("*:D");
//        cmdLine.add("logcat *:D");
//        cmdLine.add("log");
        System.out.println(cmdLine.toArray(new String[cmdLine.size()]));
        Process exec = null;
        try {
          exec = Runtime.getRuntime().exec(cmdLine.toArray(new String[cmdLine.size()]));
//          exec = Runtime.getRuntime().exec("logcat -v time *:D");
          //获取执行命令后的输入流
          InputStream inputStream = exec.getInputStream();
          InputStreamReader buInputStreamReader = new InputStreamReader(inputStream);//装饰器模式
          BufferedReader bufferedReader = new BufferedReader(buInputStreamReader);//直接读字符串
          String str = null;
          while((str = bufferedReader.readLine())!=null){

            if (str.contains(String.valueOf(pid))){
              Level level = getLevel(str);
              Log log=new Log(level,str);

              callback.onLog(log);

            }

          }
          //吐司
//          Log.i("日志11",sb.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();



  }

  private static Level getLevel(String str){
    String[] split = str.split(" ");
    if (split.length>3){
      String s = split[2];
      if (s.startsWith("D")){
        return Level.DEBUG;
      }else if (s.startsWith("I")){
        return Level.INFO;
      }else if (s.startsWith("W")){
        return Level.WARN;
      }else if (s.startsWith("E")){
        return Level.ERROR;
      }
    }
    return Level.VERBOSE;
  }

  public static void clearLog(){
    try {
      Runtime.getRuntime().exec("logcat -c");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
