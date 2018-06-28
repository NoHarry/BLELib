package cc.noharry.bledemo.util;

/**
 * @author NoHarry
 * @date 2018/06/15
 */
public class MethodUtils {
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }


  public  static String getHexString(byte[] value){
    if (value!=null){
      char[] chars = "0123456789ABCDEF".toCharArray();
      StringBuilder sb = new StringBuilder("");
      int bit;

      for (int i = 0; i < value.length; i++) {
        bit = (value[i] & 0x0F0) >> 4;
        sb.append(chars[bit]);
        bit = value[i] & 0x0F;
        sb.append(chars[bit]);
        if (i!=value.length-1){
          sb.append('-');
        }

      }
      return "(0x) "+sb.toString().trim();
    }else {
      return "";
    }

  }




  public static String int2HexString(int i){
    String s = Integer.toHexString(i);
    String str;
    if (s.length()%2!=0){
      str="0"+s;
    }else{
      str=s;
    }
    return str;
  }
}
