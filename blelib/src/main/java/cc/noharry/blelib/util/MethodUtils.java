package cc.noharry.blelib.util;

import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.exception.MacFormatException;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public class MethodUtils {
  public static boolean checkScanConfig(BleScanConfig config) throws MacFormatException {
    String[] deviceMac = config.getDeviceMac();
    for (String s:deviceMac){
      String[] mac = s.split(":");
      if (mac.length!=6||s.length()!=17){
        throw new MacFormatException("Invalid MAC string:"+s);
      }
    }
    return true;
  }

}
