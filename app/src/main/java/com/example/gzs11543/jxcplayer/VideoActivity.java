package com.example.gzs11543.jxcplayer;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;



import com.example.player.player.JxcPlayer;
import com.example.player.player.listener.PlayerListener;
import com.example.player.player.util.OrientationDetector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

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
        orientationDetector.setFullScreenButton(OrientationDetector.isPortrait(this));
        orientationDetector.setLastOrientationType(OrientationDetector.SCREEN_ORIENTATION_PORTRAIT);
        jxcPlayer.setVideoPath(videoPath);
        jxcPlayer.setCover(cover);
        jxcPlayer.setVideoSize(videoWidth,videoHeight);
        jxcPlayer.setPortrait(OrientationDetector.isPortrait(this));
        jxcPlayer.setPlayerListener(new PlayerListener() {
            @Override
            public void toggleScreen(boolean isFullScreenButton) {
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
        if(!OrientationDetector.isPortrait(this)){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jxcPlayer.onDestroy();
    }
}
