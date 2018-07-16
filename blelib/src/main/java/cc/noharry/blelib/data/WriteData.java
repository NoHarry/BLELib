package cc.noharry.blelib.data;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/06/26
 */
public class WriteData extends Data {
  private byte[] value;
  private LinkedBlockingQueue<byte[]> mQueue=new LinkedBlockingQueue<>();
  private AtomicBoolean isAutoSplit=new AtomicBoolean(false);
  public static final int DEFAULT_SIZE=20;
  private int packSize=DEFAULT_SIZE;
  private int totalPackSize;
  private int reamainPackSize;

  @Override
  public void setValue(byte[] value) {
    setValue(value,false);
  }


  public void setMTUSize(int mtuSize) {
    if (mtuSize<=23){
      this.packSize=20;
    }else if (mtuSize>=517){
      this.packSize=512;
    }else {
      this.packSize = mtuSize;
    }
    handleValue(value);
  }

  public void setValue(byte[] value,boolean isAutoSplit) {
    this.isAutoSplit.set(isAutoSplit);
    this.value=value;
    handleValue(value);
  }

  public void setValue(List<byte[]> value) {
    mQueue.clear();
    if (!value.isEmpty()){
      for (byte[] bytes:value){
        mQueue.offer(bytes);
      }
    }
    totalPackSize=mQueue.size();
  }

  private void handleValue(byte[] value) {
    mQueue.clear();
    if (value!=null){
      if (isAutoSplit.get()){
        int size=value.length%packSize==0?(value.length/packSize):(value.length/packSize)+1;
        for (int i=0;i<size;i++){
          int dataSize=(i==size-1)?value.length-packSize*i:packSize;
          byte[] data=new byte[dataSize];
          System.arraycopy(value,i*dataSize,data,0,dataSize);
          mQueue.offer(data);
        }
        totalPackSize=mQueue.size();
      }else {
        mQueue.offer(value);
        totalPackSize=mQueue.size();
      }
    }
  }

  @Override
  public byte[] getValue() {
    byte[] value=null;
    if (!mQueue.isEmpty()){
      value=mQueue.poll();
    }
    reamainPackSize=mQueue.size();
    return value;
  }

  public boolean isFinished(){
    return mQueue.isEmpty();
  }

  public int getTotalPackSize() {
    return totalPackSize;
  }

  public int getReamainPackSize() {
    return reamainPackSize;
  }


}
