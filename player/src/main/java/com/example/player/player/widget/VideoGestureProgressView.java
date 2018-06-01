package com.example.player.player.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.player.R;
import com.example.player.player.util.StringUtil;


public class VideoGestureProgressView extends LinearLayout {

    private TextView progressTextView;
    private View progressView;
    private int lastProgress = -1;


    public VideoGestureProgressView(Context context) {
        this(context,null);
    }

    public VideoGestureProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoGestureProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.video_progress_layout,this);
        progressTextView = (TextView)findViewById(R.id.progress_textview_gesture);
        progressView = (View)findViewById(R.id.progress_view_gesture);
    }

    public int setProgress(int progress,int duration,int seekBarProgress){
        if(progressView.getVisibility() != View.VISIBLE){
            progressView.setVisibility(View.VISIBLE);
        }
        if(lastProgress == -1){
            lastProgress = seekBarProgress;
        }
        int currentProgress = lastProgress - progress;
        if(currentProgress > duration){
            currentProgress = duration;
        }else if(currentProgress < 0){
            currentProgress = 0;
        }
        progressTextView.setText(StringUtil.stringToTime(currentProgress) + "/" + StringUtil.stringToTime(duration));
        lastProgress = currentProgress;
        return currentProgress;
    }

    public void hide(){
        lastProgress = -1;
        progressView.setVisibility(GONE);
    }


}
