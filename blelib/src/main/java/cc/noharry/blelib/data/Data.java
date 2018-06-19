package cc.noharry.blelib.data;

/**
 * @author NoHarry
 * @date 2018/05/28
 */

public class Data {
  private byte[] value;

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }


  @Override
  public  String toString(){
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
  }





}
