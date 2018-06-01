package com.example.gzs11543.jxcplayer.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ImageLoaderUtil {
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
    public static void glideImageLoader(Context context, String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    public static void glideImageLoader(final Context context, String path, final ImageView imageView, final int width, final int height){
        int screenWidth = getScreenWidth(context);
        int newWidth = screenWidth/2;
        int newHeight = newWidth * height / width;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(newWidth,newHeight);
        imageView.setLayoutParams(params);
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

}
