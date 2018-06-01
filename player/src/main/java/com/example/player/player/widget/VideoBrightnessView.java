package com.example.player.player.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.player.R;

public class VideoBrightnessView extends LinearLayout {
    private View brightnessView;
    private ProgressBar progressBar;
    public VideoBrightnessView(Context context) {
        this(context,null);
    }

    public VideoBrightnessView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoBrightnessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.video_brightness_layout,this);
        progressBar  = (ProgressBar)findViewById(R.id.brightness_bar);
        brightnessView = (View)findViewById(R.id.brightness_view);
    }

    public int setBrightness(float increasePercent,int systemBrightness,int maxBrightness){
        if(brightnessView.getVisibility() != VISIBLE){
            brightnessView.setVisibility(VISIBLE);
            progressBar.setMax(maxBrightness);
        }
        int currentBrightness = (int) (systemBrightness + increasePercent * maxBrightness);
        if(currentBrightness > maxBrightness){
            currentBrightness = maxBrightness;
        }else if(currentBrightness < 0){
            currentBrightness = 0;
        }
        progressBar.setProgress(currentBrightness);
        return currentBrightness;
    }

    public void hide(){
        brightnessView.setVisibility(GONE);
    }
}
