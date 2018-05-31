package com.example.player.player.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoaderUtil {
    public static void glideImageLoader(Context context, String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .into(imageView);
    }
}
