package com.example.player.player.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.player.R;

public class VideoVolumeView extends LinearLayout {
    private View volumeView;
    private ProgressBar progressBar;
    private int lastVolume = -1;
    public VideoVolumeView(Context context) {
        this(context,null);
    }

    public VideoVolumeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoVolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.video_volume_layout,this);
        progressBar  = (ProgressBar)findViewById(R.id.volume_bar);
        volumeView = (View)findViewById(R.id.volume_view);
    }

    public int setVolume(float increasePercent,int systemVolume,int maxVolume){
        if(volumeView.getVisibility() != VISIBLE){
            volumeView.setVisibility(VISIBLE);
            progressBar.setMax(maxVolume);
        }
        int currentVolume = (int) (systemVolume + increasePercent * maxVolume);
        if(currentVolume > maxVolume){
            currentVolume = maxVolume;
        }else if(currentVolume < 0){
            currentVolume = 0;
        }
        progressBar.setProgress(currentVolume);
        //lastVolume = currentVolume;
        return currentVolume;

    }

    public void hide(){
        lastVolume = -1;
        volumeView.setVisibility(GONE);
    }
}
