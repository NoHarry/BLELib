package cc.noharry.bledemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author NoHarry
 * @date 2018/10/31
 */
public class StatuData implements Parcelable {
  public int statuCode;
  public String msg;

  public StatuData(int statuCode, String msg) {
    this.statuCode = statuCode;
    this.msg = msg;
  }

  protected StatuData(Parcel in) {
    statuCode = in.readInt();
    msg = in.readString();
  }

  public static final Creator<StatuData> CREATOR = new Creator<StatuData>() {
    @Override
    public StatuData createFromParcel(Parcel in) {
      return new StatuData(in);
    }

    @Override
    public StatuData[] newArray(int size) {
      return new StatuData[size];
    }
  };

  public int getStatuCode() {
    return statuCode;
  }

  public void setStatuCode(int statuCode) {
    this.statuCode = statuCode;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "StatuData{" +
        "statuCode=" + statuCode +
        ", msg='" + msg + '\'' +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(statuCode);
    dest.writeString(msg);
  }
}
