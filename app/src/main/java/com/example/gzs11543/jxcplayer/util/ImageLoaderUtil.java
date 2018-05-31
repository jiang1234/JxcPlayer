package com.example.gzs11543.jxcplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderUtil {
    public static void glideImageLoader(Context context, String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    public static void universalImageLoader(Context context, String path, ImageView imageView){
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions imageOptions = DisplayImageOptions.createSimple();
        ImageLoader.getInstance().displayImage(path,imageView,imageOptions);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(path);

    }

    public static Bitmap universalBitmapLoader(Context context, String path){
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
        //DisplayImageOptions imageOptions = DisplayImageOptions.createSimple();
        //ImageLoader.getInstance().displayImage(path,imageView,imageOptions);
        return ImageLoader.getInstance().loadImageSync(path);

    }


    public static void glideImageLoader(Context context, String path, final Canvas canvas, final Paint paint){
        final Bitmap[] bitmap = new Bitmap[1];
        Glide.with(context).load(path).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //image.setImageBitmap(resource);
                canvas.drawBitmap(resource,0,0,paint);
                bitmap[0] =  resource;
            }
        });
    }
}
