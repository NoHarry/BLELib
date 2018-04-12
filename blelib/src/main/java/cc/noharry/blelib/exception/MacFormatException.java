package cc.noharry.blelib.exception;

import android.util.Log;

/**
 * @author NoHarry
 * @date 2018/04/10
 */

public class MacFormatException extends Exception {

  public MacFormatException(String message) {
    super(message);
    Log.e("BLELIB","MacFormatException:"+message);
  }
}
