package cc.noharry.blelib.util;

import android.os.ParcelUuid;
import cc.noharry.blelib.ble.scan.BleScanConfig;
import cc.noharry.blelib.data.BleDevice;
import cc.noharry.blelib.data.ScanRecord;
import cc.noharry.blelib.exception.MacFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

  public static boolean checkBleDevice(BleScanConfig config,BleDevice device){
    String[] deviceMac = config.getDeviceMac();
    String[] deviceNames = config.getDeviceNames();
    UUID[] uuids = config.getUUIDS();
    List<ParcelUuid> configUUIDS=new ArrayList<>();
    ScanRecord scanRecord=ScanRecord.parseFromBytes(device.getScanRecord());
//    L.i("扫描到设备:"+scanRecord.toString());
    if (uuids!=null&&uuids.length!=0){
      for (UUID uuid:uuids){
        configUUIDS.add(new ParcelUuid(uuid));
      }
    }


    if (!configUUIDS.isEmpty()){
      List<ParcelUuid> parcelUuids = scanRecord.getServiceUuids();
      boolean isUUIDFit=false;
      loopConfig:
      for (ParcelUuid configUUID:configUUIDS){
        if (parcelUuids!=null&&!parcelUuids.isEmpty()){
          for (ParcelUuid deviceUUID:parcelUuids){
            if (configUUID.equals(deviceUUID)){
              isUUIDFit=true;
              break loopConfig;
            }
          }
        }
      }
      if (!isUUIDFit){
        return false;
      }

    }


    if (deviceMac!=null&&deviceMac.length!=0){
      boolean isMacFit=false;
      for (String n:deviceMac){
        if (device.getMac().equalsIgnoreCase(n)){
          isMacFit=true;
          break;
        }
      }
      if (!isMacFit){
        return false;
      }
    }


    if (deviceNames!=null&&deviceNames.length!=0){
      if (config.isFuzzy()){
        boolean isFuzzyNameFit=false;
        for (String s:deviceNames){
          if (device.getName().contains(s)){
            isFuzzyNameFit=true;
            break;
          }
        }
        if (!isFuzzyNameFit){
          return false;
        }
      }else {
        boolean isNameFit=false;
        for (String s:deviceNames){
          if (device.getName().equalsIgnoreCase(s)){
            isNameFit=true;
            break;
          }
        }
        if (!isNameFit){
          return false;
        }
      }
    }

    return true;
  }


  public static boolean checkBleDeviceNew(BleScanConfig config,BleDevice device){
    String[] deviceMac = config.getDeviceMac();
    String[] deviceNames = config.getDeviceNames();
    if (deviceMac!=null&&deviceMac.length!=0){
      boolean isMacFit=false;
      for (String n:deviceMac){
        if (device.getMac().equalsIgnoreCase(n)){
          isMacFit=true;
          break;
        }
      }
      if (!isMacFit){
        return false;
      }
    }


    if (deviceNames!=null&&deviceNames.length!=0){
      if (config.isFuzzy()){
        boolean isFuzzyNameFit=false;
        for (String s:deviceNames){
          if (device.getName()==null){
            return false;
          }
          if (device.getName().contains(s)){
            isFuzzyNameFit=true;
           break;
          }
        }
        if (!isFuzzyNameFit){
          return false;
        }
      }else {
        boolean isNameFit=false;
        for (String s:deviceNames){
          if (device.getName().equalsIgnoreCase(s)){
           isNameFit=true;
           break;
          }
        }
        if (!isNameFit){
          return false;
        }
      }
    }

    return true;
  }

}
