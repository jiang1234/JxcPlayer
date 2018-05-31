package com.example.player.player.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ViewSizeUtil {
    private static final int STATE_VIEW_VERTICAL = 0;
    private static final int STATE_VIEW_HORIZONTAL = 1;
    public static int getScreenWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
    public static Bitmap changeBitmapSize(Bitmap bitmap,int newWidth,int newHeight){
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();
        float scaleHeight = ((float)newHeight)/oldHeight;
        float scaleWidth = ((float)newWidth)/oldWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0,
                oldWidth, oldHeight, matrix, true);
    }

    public static void changeSurfaceViewSize(Context context,SurfaceView surfaceView,boolean isPorirait,int videoWidth,int videoHeight){
        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);
        if(isPorirait){
            int surfaceViewWeight = screenWidth;
            int surfaceViewHeight = videoHeight*surfaceViewWeight/videoWidth;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(surfaceViewWeight,surfaceViewHeight);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(lp);
            surfaceView.setVisibility(View.VISIBLE);
        }else{
            if(videoWidth < videoHeight){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenHeight*videoWidth/videoHeight,screenHeight);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                surfaceView.setLayoutParams(lp);
                surfaceView.setVisibility(View.VISIBLE);
            }else{
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                surfaceView.setLayoutParams(lp);
                surfaceView.setVisibility(View.VISIBLE);
            }

        }
    }

    public static void changeImageViewSize(Context context,ImageView imageView,int viewWidth,int viewHeight){
        int width = getScreenWidth(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,viewHeight*width/viewWidth);
        lp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(lp);
    }

    public static void changeImageViewSizeFitScreen(Context context,ImageView imageView){
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(getScreenWidth(context),9*getScreenWidth(context)/16);
        lp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(lp);
    }
}
