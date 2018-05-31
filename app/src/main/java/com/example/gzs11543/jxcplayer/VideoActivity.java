package com.example.gzs11543.jxcplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.VolumeShaper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.player.player.JxcPlayer;
import com.example.player.player.listener.PlayerListener;
import com.example.player.player.util.OrientationDetector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {
    private static final int STATE_VIEW_VERTICAL = 0;
    private static final int STATE_VIEW_HORIZONTAL = 1;

    @BindView(R.id.player)
    JxcPlayer jxcPlayer;

    private String videoPath;
    private String cover;
    private int videoHeight;
    private int videoWidth;
    private OrientationDetector orientationDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        ButterKnife.bind(this);
        initView();
    }
    public void initView(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoPath = bundle.getString("flv");
        videoPath.replace("flv","mp4");
        cover = bundle.getString("cover");
        videoHeight = bundle.getInt("height");
        videoWidth = bundle.getInt("width");
        orientationDetector = OrientationDetector.getInstance(this);
        orientationDetector.setFullScreenButton(isPortrait());
        orientationDetector.setLastOrientationType(OrientationDetector.SCREEN_ORIENTATION_PORTRAIT);
        jxcPlayer.setVideoPath(videoPath);
        jxcPlayer.setCover(cover);
        jxcPlayer.setViewState(STATE_VIEW_VERTICAL);
        jxcPlayer.setVideoSize(videoWidth,videoHeight);
        jxcPlayer.setPortrait(isPortrait());
        jxcPlayer.setPlayerListener(new PlayerListener() {
            @Override
            public void toggleScreen(boolean isFullScreenButton) {
                /*if(isPortrait){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }*/
                //isFullScreenButton表示在竖屏状态下点击了全屏按钮
                orientationDetector.toggleScreen(isFullScreenButton);
            }

            @Override
            public void isPlayerPrepare() {
                orientationDetector.setPrepared(true);
            }
        });
        jxcPlayer.init();


    }

    public boolean isPortrait(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return;
        }
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if(!isPortrait()){
            orientationDetector.toggleScreen(false);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jxcPlayer.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        orientationDetector.start(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        orientationDetector.stop();
        jxcPlayer.onStop();
    }
}
