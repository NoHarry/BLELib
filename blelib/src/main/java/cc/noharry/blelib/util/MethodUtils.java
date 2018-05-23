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




/*
  public static ParsedAd parseData(byte[] adv_data) {
    ParsedAd parsedAd = new ParsedAd();
    ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
    while (buffer.remaining() > 2) {
      byte length = buffer.get();
      if (length == 0)
        break;

      byte type = buffer.get();
      length -= 1;
      switch (type) {
        case 0x01: // Flags
          parsedAd.flags = buffer.get();
          length--;
          break;
        case 0x02: // Partial list of 16-bit UUIDs
        case 0x03: // Complete list of 16-bit UUIDs
        case 0x14: // List of 16-bit Service Solicitation UUIDs
          while (length >= 2) {
            parsedAd.uuids.add(UUID.fromString(String.format(
                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
            length -= 2;
          }
          break;
        case 0x04: // Partial list of 32 bit service UUIDs
        case 0x05: // Complete list of 32 bit service UUIDs
          while (length >= 4) {
            parsedAd.uuids.add(UUID.fromString(String.format(
                "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
            length -= 4;
          }
          break;
        case 0x06: // Partial list of 128-bit UUIDs
        case 0x07: // Complete list of 128-bit UUIDs
        case 0x15: // List of 128-bit Service Solicitation UUIDs
          while (length >= 16) {
            long lsb = buffer.getLong();
            long msb = buffer.getLong();
            parsedAd.uuids.add(new UUID(msb, lsb));
            length -= 16;
          }
          break;
        case 0x08: // Short local device name
        case 0x09: // Complete local device name
          byte sb[] = new byte[length];
          buffer.get(sb, 0, length);
          length = 0;
          parsedAd.localName = new String(sb).trim();
          break;
        case (byte) 0xFF: // Manufacturer Specific Data
          parsedAd.manufacturer = buffer.getShort();
          length -= 2;
          break;
        default: // skip
          break;
      }
      if (length > 0) {
        buffer.position(buffer.position() + length);
      }
    }
    return parsedAd;
  }
*/



}
