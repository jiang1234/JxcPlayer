package com.example.player.player.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;


public class OrientationDetector {
    private final static String TAG = "OrientationDetector";
    public final static int SCREEN_UNKNOWN = -1;
    public final static int SCREEN_ORIENTATION_PORTRAIT = 1;
    public final static int SCREEN_ORIENTATION_LANDSCAPE = 2;
    public final static int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 3;
    public final static int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 4;

    private volatile static OrientationDetector instance;
    private int lastOrientationType;
    private Context context;
    private int orientationType;
    private Activity activity;
    private boolean isFullScreenButton;
    private OrientationEventListener orientationRotateListener;
    private OrientationEventListener orientationToggleListener;
    private boolean isPrepared;


    private OrientationDetector(Context context){
        this.context = context;

        orientationRotateListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN){
                    orientationType = ORIENTATION_UNKNOWN;
                    return;
                }
                if(orientation > 350 || orientation< 10){
                    orientationType = SCREEN_ORIENTATION_PORTRAIT;
                } else if(orientation > 80 && orientation < 100){
                    orientationType = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if(orientation > 170 && orientation < 190){
                    orientationType = SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if(orientation > 260 && orientation < 280){
                    orientationType = SCREEN_ORIENTATION_LANDSCAPE;
                }
                Log.i(TAG, "onOrientationChanged: "+orientation + "orientationType="+orientationType);
                changeOrientation(orientationType);
            }
        };

        orientationToggleListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN){
                    orientationType = ORIENTATION_UNKNOWN;
                    return;
                }
                if(orientation > 350 || orientation< 10){
                    orientationType = SCREEN_ORIENTATION_PORTRAIT;
                } else if(orientation > 80 && orientation < 100){
                    orientationType = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if(orientation > 170 && orientation < 190){
                    orientationType = SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if(orientation > 260 && orientation < 280){
                    orientationType = SCREEN_ORIENTATION_LANDSCAPE;
                }
                //changeOrientation(orientationType);
                //手机其实处于竖屏状态
                if(orientationType == SCREEN_ORIENTATION_PORTRAIT || orientationType == SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                    //isFullScreenButton表示手机处于竖屏，想要进入全屏
                    if(isFullScreenButton){
                        //手机状态与按钮状态一置
                        orientationRotateListener.enable();
                        orientationToggleListener.disable();
                    }

                }

                if(orientationType == SCREEN_ORIENTATION_LANDSCAPE || orientationType == SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                    //isFullScreenButton表示手机处于竖屏，想要进入全屏
                    if(!isFullScreenButton){
                        //手机状态与按钮状态一置
                        orientationRotateListener.enable();
                        orientationToggleListener.disable();
                    }

                }
            }
        };
    }

    public static OrientationDetector getInstance(Context context){
        if(instance == null){
            synchronized (OrientationDetector.class){
                if(instance == null){
                    instance = new OrientationDetector(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void start(Activity activity){
        this.activity = activity;
        orientationRotateListener.enable();
    }
    public void stop(){
        activity = null;
        orientationRotateListener.disable();
    }

    public void changeOrientation(int orientationType){
        if(isPrepared){
            switch (orientationType){
                case SCREEN_ORIENTATION_PORTRAIT:
                    if(lastOrientationType != SCREEN_ORIENTATION_PORTRAIT){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        lastOrientationType = SCREEN_ORIENTATION_PORTRAIT;
                    }
                    break;
                case SCREEN_ORIENTATION_LANDSCAPE:
                    if(lastOrientationType != SCREEN_ORIENTATION_LANDSCAPE){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        lastOrientationType = SCREEN_ORIENTATION_LANDSCAPE;
                    }
                    break;
                case SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    if(lastOrientationType != SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        lastOrientationType = SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    }
                    break;
                case SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    if(lastOrientationType != SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        lastOrientationType = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void toggleScreen(boolean isFullScreenButton) {
        //点击按钮切换时，停止rotate监听器,打开Toggle监听器，Toggle在手机位置与屏幕显示的状态重合时，重新开启rotate监听器
        orientationRotateListener.disable();
        orientationToggleListener.enable();
        //更新按钮是否点击的状态

        if(isFullScreenButton){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        this.isFullScreenButton = isFullScreenButton;
    }

    public static boolean isPortrait(Context context){
        if( context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        return false;
    }

    public void setFullScreenButton(boolean fullScreenButton) {
        isFullScreenButton = fullScreenButton;
    }

    public void setLastOrientationType(int lastOrientationType) {
        this.lastOrientationType = lastOrientationType;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }
}
