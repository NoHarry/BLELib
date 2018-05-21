package cc.noharry.blelib.ble.connect;

/**
 * @author NoHarry
 * @date 2018/05/21
 */

public class BleConnector {

  private BleConnector(BleClient bleClient){

  }

  public static BleConnector getInstance(BleClient bleClient){
    return new BleConnector(bleClient);
  }

}
