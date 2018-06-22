package cc.noharry.bledemo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import cc.noharry.bledemo.R;
import cc.noharry.blelib.util.L;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoHarry
 * @date 2018/06/21
 */
public class ToggleImageView extends android.support.v7.widget.AppCompatImageView implements
    OnClickListener {


  private int mSwitchOnDrawable;
  private int mSwitchOffDrawable;
  private boolean mIsSwitchOn;
  private Context mContext;
  private AtomicBoolean isSwitchOn=new AtomicBoolean(true);
  private AnimatedVectorDrawable mSwitchAni;
  public static final int SWITCH_MODE_ON=0;
  public static final int SWITCH_MODE_OFF=1;

  public ToggleImageView(Context context) {
    this(context,null);
  }

  public ToggleImageView(Context context, AttributeSet attrs) {
    this(context, attrs,0);
  }

  public ToggleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext=context;
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ToggleImageView);
    mSwitchOnDrawable = array.getResourceId(R.styleable.ToggleImageView_switch_on_drawable, 0);
    mSwitchOffDrawable = array.getResourceId(R.styleable.ToggleImageView_switch_off_drawable, 0);
    mIsSwitchOn = array.getBoolean(R.styleable.ToggleImageView_is_switch_on, true);
    isSwitchOn.set(mIsSwitchOn);
    array.recycle();

    L.i("mSwitchOnDrawable:"+mSwitchOnDrawable);
    L.i("mSwtichOffDrawable:"+mSwitchOffDrawable);
    L.i("mIsSwitchOn:"+mIsSwitchOn);
    initView();
    initEvent();
  }

  private void initEvent() {
    setOnClickListener(this);
  }

  private void initView() {
    if (isSwitchOn.get()){
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOffDrawable);
    }else {
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOnDrawable);
    }
    setImageDrawable(mSwitchAni);
  }

  private void updateView(){
    if (isSwitchOn.get()){
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOffDrawable);
      isSwitchOn.set(false);
    }else {
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOnDrawable);
      isSwitchOn.set(true);
    }
    setImageDrawable(mSwitchAni);
  }

  private void switchView(boolean b){
    L.i("isSwitchOn:"+isSwitchOn.get());
    isSwitchOn.set(b);
    if (isSwitchOn.get()){
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOnDrawable);
    }else {
      mSwitchAni = (AnimatedVectorDrawable) mContext.getDrawable(mSwitchOffDrawable);
    }
    setImageDrawable(mSwitchAni);
  }


  @Override
  public void onClick(View v) {
    updateView();
    mSwitchAni.start();
  }

  public boolean getIsSwitchOn() {
    return isSwitchOn.get();
  }

  public ToggleImageView setSwitchMode(int switchMode){
    switch(switchMode){
      case SWITCH_MODE_ON:
        switchView(true);
        /*if (!isSwitchOn.get()){
          switchView(true);
          doSwitch();
        }*/
        break;
      case SWITCH_MODE_OFF:
        switchView(false);
        /*if (isSwitchOn.get()){
          switchView(false);
          doSwitch();
        }*/

        break;
    }
    return this;
  }

  public ToggleImageView doSwitch(){
    if (mSwitchAni!=null){
      mSwitchAni.start();
    }
    return this;
  }
}
