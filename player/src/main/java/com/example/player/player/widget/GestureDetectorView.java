package com.example.player.player.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class GestureDetectorView extends FrameLayout implements GestureDetector.OnGestureListener{
    private final static String TAG = "GestureDetectorView";
    public final static int NO_GESTURE = -1;
    public final static int GESTURE_BEHAVIOR_PROGRESS = 0;
    public final static int GESTURE_BEHAVIOR_VOLUME = 1;
    public final static int GESTURE_BEHAVIOR_BRIGHTNESS = 2;


    private GestureDetector detector;
    protected int gestureBehavior;
    private AudioManager audioManager;
    protected int maxVolume;
    protected int maxBrightness;
    protected int lastBrightness;
    protected int lastVolume;

    public GestureDetectorView(@NonNull Context context) {
        this(context,null);
    }

    public GestureDetectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GestureDetectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        detector = new GestureDetector(getContext(),this);
        audioManager = (AudioManager)(context.getSystemService(Context.AUDIO_SERVICE));
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        maxBrightness = 255;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                finishGesture(gestureBehavior);
            default:break;
        }
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        gestureBehavior = NO_GESTURE;
        lastBrightness = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        lastBrightness = (int) (((Activity)getContext()).getWindow().getAttributes().screenBrightness*maxBrightness);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //子类重写
    }
    //轻击屏幕并立刻抬起
    //轻击屏幕唤醒控制条
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int width = getWidth();
        int height = getHeight();
        if(width <= 0 || height <= 0){
            return false;
        }
        if(gestureBehavior == NO_GESTURE) {
            float dx = e2.getX() - e1.getX();
            float dy = e2.getY() - e1.getY();
            if (Math.abs(dy) >= Math.abs(dx)) {
                //竖向调节
                if (e1.getX() < width / 2) {
                    //调节亮度
                    gestureBehavior = GESTURE_BEHAVIOR_BRIGHTNESS;
                } else {
                    //调节声音
                    gestureBehavior = GESTURE_BEHAVIOR_VOLUME;
                }
            } else {
                gestureBehavior = GESTURE_BEHAVIOR_PROGRESS;
            }
        }
            switch (gestureBehavior){
                case GESTURE_BEHAVIOR_PROGRESS:
                    float percent = distanceX/width;
                    seekProgressByGesture(percent);
                    break;
                case GESTURE_BEHAVIOR_BRIGHTNESS:
                    //设高度的50%即可完全调整亮度
                    float brightnessPercent = distanceY/((0.5f)*height);
                    seekBrightnessByGesture(brightnessPercent);
                    break;
                case GESTURE_BEHAVIOR_VOLUME:
                    //设高度的50%即可完全调整声音
                    float volumePercent = distanceY/((0.5f)*height);
                    seekVolumeByGesture(volumePercent);
                    break;
                default:break;
            }


        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    protected  abstract void seekProgressByGesture(float percent);

    protected  void seekVolumeByGesture(float volumePercent){
        int currentVolume = Math.round(volumePercent*maxVolume)+lastVolume;
        if(currentVolume < 0){
            currentVolume = 0;
        }else if(currentVolume > maxVolume){
            currentVolume = maxVolume;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
        lastVolume = currentVolume;
    }

    protected  void seekBrightnessByGesture(float percent){
        try {
            if (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            int currentBrightness = (int) (lastBrightness + percent * maxBrightness);
            if(currentBrightness < 0){
                currentBrightness = 0;
            }else if(currentBrightness > maxBrightness){
                currentBrightness = maxBrightness;
            }
            lastBrightness = currentBrightness;
            Window window = ((Activity)getContext()).getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = currentBrightness / (float) maxBrightness;
            window.setAttributes(params);
            lastBrightness = currentBrightness;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected abstract void finishGesture(int gestureBehavior);

}
