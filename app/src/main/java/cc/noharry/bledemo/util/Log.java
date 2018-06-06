package cc.noharry.bledemo.util;


import android.os.Parcel;
import android.os.Parcelable;
import cc.noharry.bledemo.util.LogUtil.Level;

/**
 * @author NoHarry
 * @date 2018/06/06
 */
public class Log implements Parcelable {
  private Level mLevel;
  private String mContent;

  public Log(Level level, String content) {
    mLevel = level;
    mContent = content;
  }

  protected Log(Parcel in) {
    mContent = in.readString();
  }

  public static final Creator<Log> CREATOR = new Creator<Log>() {
    @Override
    public Log createFromParcel(Parcel in) {
      return new Log(in);
    }

    @Override
    public Log[] newArray(int size) {
      return new Log[size];
    }
  };

  public Level getLevel() {
    return mLevel;
  }

  public void setLevel(Level level) {
    mLevel = level;
  }

  public String getContent() {
    return mContent;
  }

  public void setContent(String content) {
    mContent = content;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mContent);
  }

  @Override
  public String toString() {
    return "Log{" +
        "mLevel=" + mLevel +
        ", mContent='" + mContent + '\'' +
        '}';
  }
}
