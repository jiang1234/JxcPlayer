package com.example.player.player.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.player.R;
import com.example.player.player.JxcPlayer;
import com.example.player.player.listener.VideoControlListener;
import com.example.player.player.receiver.NetworkBroadcastReceiver;
import com.example.player.player.util.ImageLoaderUtil;
import com.example.player.player.util.StringUtil;
import com.example.player.player.util.ViewSizeUtil;


public class VideoControllerView extends FrameLayout {
    private static final String TAG = "VideoControllerView";
    public static final int DEFAULT_CONTROLLER_SHOW_TIME = 3000;
    private ImageButton playButton;
    private ImageButton centerPlayButton;
    private SeekBar seekBar;
    private ImageButton fullScreenButton;
    private VideoControlListener videoControlListener;
    private ImageView coverImageView;
    private ImageButton reStartButton;
    private TextView finishTextView;
    private View controllerBackground;
    private VideoErrorView videoErrorView;
    private int seekBarMax;
    private boolean isStart;
    private String cover;
    private View videoControllerBottom;
    private JxcPlayer player;
    private TextView durationTextView;
    private int position;
    private boolean isPortrait;


    // private int bufferPercentage;
    private int duration;

    private boolean isControllerBottomShow;
    private int errorType;


    public VideoControllerView(@NonNull Context context) {
        this(context,null);
    }

    public VideoControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.video_controller_layout,this);
        playButton = (ImageButton)findViewById(R.id.play);
        centerPlayButton = (ImageButton)findViewById(R.id.play_center);
        seekBar = (SeekBar)findViewById(R.id.progress);
        fullScreenButton = (ImageButton)findViewById(R.id.full_screen);
        coverImageView = (ImageView)findViewById(R.id.cover);
        reStartButton = (ImageButton)findViewById(R.id.restart);
        finishTextView = (TextView) findViewById(R.id.finish);
        videoControllerBottom = (View)findViewById(R.id.video_controller_bottom);
        durationTextView = (TextView)findViewById(R.id.duration);
        controllerBackground = (View) findViewById(R.id.controller_background);
        videoErrorView = (VideoErrorView)findViewById(R.id.controller_error);
    }


    public void init(){
        if(cover != null || !cover.isEmpty()){
            ViewSizeUtil.changeImageViewSizeFitScreen(getContext(),coverImageView);
            ImageLoaderUtil.glideImageLoader(getContext(),cover,coverImageView);
        }

        if(centerPlayButton != null){
            centerPlayButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    hideStartView();
                    isStart = true;
                    videoControlListener.prepareVideo();
                }
            });
        }
        if(seekBar != null){
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    String durationTime = StringUtil.stringToTime(progress) + "/" + StringUtil.stringToTime(duration);
                    durationTextView.setText(durationTime);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //停止定时功能
                    removeCallbacks(showRunnable);
                    removeCallbacks(hideRunnable);
                    videoControllerBottom.setVisibility(View.VISIBLE);
                    isControllerBottomShow = true;

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //
                    Log.i(TAG, "onStopTrackingTouch: "+StringUtil.stringToTime(seekBar.getProgress()));
                    //将seekBar的position传递给player
                    videoControlListener.seekVideo(seekBar.getProgress());
                    //showControllerBottom(DEFAULT_CONTROLLER_SHOW_TIME);

                }
            });
        }

        if(playButton != null){
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isStart){
                        isStart = false;
                        playButton.setBackgroundResource(R.drawable.play);
                        videoControlListener.pauseVideo();
                    }else{
                        isStart = true;
                        playButton.setBackgroundResource(R.drawable.pause);
                        videoControlListener.startVideo();
                    }

                }
            });
        }
        if(reStartButton != null){
            reStartButton.setOnClickListener(new OnClickListener(

            ) {
                @Override
                public void onClick(View v) {
                    hideFinishView();
                    isStart = true;
                    videoControlListener.restartVideo();
                }
            });
        }

        if(fullScreenButton != null){
            fullScreenButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoControlListener.toggleScreen(isPortrait);
                    if(isPortrait){
                        isPortrait = false;
                        fullScreenButton.setBackgroundResource(R.drawable.small_screen);
                    }else{
                        isPortrait = true;
                        fullScreenButton.setBackgroundResource(R.drawable.full_screen);
                    }
                }
            });
        }
    }

    public void setOnVideoControllerView(VideoControlListener videoControlListener) {
        this.videoControlListener = videoControlListener;
        videoErrorView.setVideoControlListener(videoControlListener);
    }

    public void setSeekBarMax(int seekBarMax) {
        Log.i(TAG, "setSeekBarMax: "+seekBarMax);
        seekBar.setMax(seekBarMax);
        duration = seekBarMax;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void updatePosition(){
        videoControlListener.updateVideoPosition();
        Log.i(TAG, "updatePosition: updatePosition=" + StringUtil.stringToTime(position));
        seekBar.setProgress(position);
       // seekBar.setSecondaryProgress(duration*bufferPercentage);
        String durationTime = StringUtil.stringToTime(position) + "/" + StringUtil.stringToTime(duration);
       // Log.i(TAG, "updatePosition: position" +StringUtil.stringToTime(position) +"duration"+ "/" + StringUtil.stringToTime(duration));
        durationTextView.setText(durationTime);
    }

    public void hideStartView(){
        coverImageView.setVisibility(View.GONE);
        centerPlayButton.setVisibility(View.GONE);
        controllerBackground.setVisibility(View.GONE);
    }
    public void showFinishView(){
        hideControllerBottom();
        controllerBackground.setVisibility(View.VISIBLE);
        reStartButton.setVisibility(View.VISIBLE);
        finishTextView.setVisibility(View.VISIBLE);
    }

    public void hideFinishView(){
        //showController(DEFAULT_CONTROLLER_SHOW_TIME);
        reStartButton.setVisibility(View.GONE);
        finishTextView.setVisibility(View.GONE);
        controllerBackground.setVisibility(View.GONE);
    }

    public void showControllerBottom(int time){
        if(videoControllerBottom.getVisibility() != View.VISIBLE){
            videoControllerBottom.setVisibility(View.VISIBLE);
        }
        removeCallbacks(hideRunnable);
        post(showRunnable);
        if(time > 0){
            postDelayed(hideRunnable,time);
        }
    }

    public void hideControllerBottom(){
        removeCallbacks(showRunnable);
        post(hideRunnable);
    }

    private Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
            isControllerBottomShow = true;

            //进度条更新，保证每过一秒的时候更新进度条，定时器
            postDelayed(showRunnable,1000-position%1000);

        }
    };

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(showRunnable);
            isControllerBottomShow = false;
            if(videoControllerBottom.getVisibility() != View.GONE){
                videoControllerBottom.setVisibility(View.GONE);
            }
        }
    };

    public boolean isControllerBottomShow() {
        return isControllerBottomShow;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    //手动切换横竖屏
    public void toggleScreen(){
        if(isPortrait){
            //若现在为竖排,设置为横
            isPortrait = false;

        }else{

        }
    }

    public boolean isPorirait(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        return false;
    }

    public void setPortrait(boolean portrait) {
        isPortrait = portrait;
    }

    public void networkStateChanged(int networkState){

        videoErrorView.setNetworkState(networkState);
        Log.i(TAG, "networkStateChanged: networkState = " + networkState);
        if(networkState == NetworkBroadcastReceiver.NO_INTERNET){
            //没有网络直接提出无网络并显示错误信息,停止播放
            videoControlListener.stopVideo();
            videoErrorView.changeErrorView(VideoErrorView.ERROR_TYPE_NO_INTERNET);
            hideControllerView();
        }else if(networkState == NetworkBroadcastReceiver.INTERENT_STATE_MOBILE){
            if(videoErrorView.getErrorType() == VideoErrorView.NO_ERROR){
                //网络状态从wifi转为移动
                videoControlListener.pauseVideo();
                videoErrorView.changeErrorView(VideoErrorView.ERROR_TYPE_MOBILE_INTERNET);
            }
        }

    }

    public void showError(){
        hideControllerView();
        videoErrorView.changeErrorView(VideoErrorView.ERROR_TYPE_OTHER_ERROR);
    }
    public void hideControllerView(){
        if(centerPlayButton.getVisibility() == View.VISIBLE){
            centerPlayButton.setVisibility(View.GONE);
        }
        hideFinishView();
        hideControllerBottom();
        removeCallbacks(showRunnable);
        videoControllerBottom.setVisibility(GONE);
    };
    public int getErrorType() {
        return videoErrorView.getErrorType();
    }

    public void changePlayButtonView(boolean isStart){
        this.isStart = isStart;
        if(isStart){
            playButton.setBackgroundResource(R.drawable.pause);
        }else{
            playButton.setBackgroundResource(R.drawable.play);
        }
    }
}
