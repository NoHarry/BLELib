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
    int[] ints=new int[value.length];
    for (int i=0;i<ints.length;i++){
      ints[i]=value[i];
    }
    StringBuilder sb=new StringBuilder();
    for (int i=0;i<ints.length;i++){
      int anInt = ints[i];
      String s = int2HexString(anInt);
      if (i==ints.length-1){
        sb.append(s+"-");
      }else {
        sb.append(s);
      }

    }
    return "(0x) " + sb.toString().trim();
  }




  private String int2HexString(int i){
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
