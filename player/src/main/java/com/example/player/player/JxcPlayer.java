package com.example.player.player;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;


import com.example.player.R;
import com.example.player.player.listener.PlayerListener;
import com.example.player.player.listener.VideoControlListener;
import com.example.player.player.receiver.NetworkBroadcastReceiver;
import com.example.player.player.util.StringUtil;
import com.example.player.player.util.ViewSizeUtil;
import com.example.player.player.weidge.VideoControllerView;

import java.io.IOException;



/**
 * 使用MediaPlayer与SurfaceView实现的视频播放器
 * @author jiang
 * @date 2018/5/25
 *
 */
public class JxcPlayer extends RelativeLayout implements GestureDetector.OnGestureListener{
    private static final String TAG = "JxcPlayer";

    private static final int DEFAULT_CONTROLLER_SHOW_TIME = 3000;
    private static final int STATE_VIEW_VERTICAL = 0;
    private static final int STATE_VIEW_HORIZONTAL = 1;
    private static final int STATE_SURFACEVIEW_CREATE = 2;
    private static final int STATE_PLAYER_IDLE = 3;
    private static final int STATE_PLAYER_PEEPARING = 4;
    private static final int STATE_PLAYER_ERROR = 5;
    private static final int STATE_PLAYER_SEEK = 6;
    private static final int STATE_PLAYER_STOP = 7;
    private static final int STATE_PLAYER_PREPARED = 8;
    private static final int STATE_PLAYER_COMPLETE = 9;


    private static final int STATE_PLAYER_PAUSE = 8;
    private static final int STATE_PLAYER_PLAY = 9;
    private boolean isPortrait;

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private VideoControllerView videoControllerView;
    private View loadingView;
    private GestureDetector detector;




    private String videoPath;
    private int playerState;
    private int surfaceViewState;
    private int viewState;
    private int position;
    //private int bufferPercentage;
    private int duration;
    private String cover;
    private Canvas canvas;
    private int videoWidth;
    private int videoHeight;
    private PlayerListener playerListener;
    private NetworkBroadcastReceiver netBroadcastReceiver;

    public JxcPlayer(@NonNull Context context) {
        this(context,null);
    }

    public JxcPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JxcPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.video_player_layout,this);
        surfaceView = (SurfaceView)findViewById(R.id.videoPlayer);
        videoControllerView = (VideoControllerView)findViewById(R.id.video_controller);
        loadingView = (View)findViewById(R.id.video_loading);
        detector = new GestureDetector(getContext(),this);

    }


    public void init(){
        initPlayer();
        //videoControllerView = new VideoControllerView(getContext());
        videoControllerView.setCover(cover);
        videoControllerView.setPortrait(isPortrait);
        videoControllerView.init();
        setPlayerState(STATE_PLAYER_IDLE);

        videoControllerView.setOnVideoControllerView(new VideoControlListener() {
            @Override
            public void startVideo() {
                if(playerState == STATE_PLAYER_PREPARED || playerState == STATE_PLAYER_PAUSE){
                    play();

                }
            }

            @Override
            public void pauseVideo() {
                if(playerState == STATE_PLAYER_PLAY){
                    pause();
                }
            }

            @Override
            public void stopVideo() {

            }

            @Override
            public void prepareVideo() {
                prepare();
                playerListener.isPlayerPrepare();
            }

            @Override
            public void seekVideo(int progress) {
                //if(canSeek()){
                //播放器跳转
                Log.i(TAG, "seekVideo: seekBar = " + StringUtil.stringToTime(progress));
                    player.seekTo(progress);

            }
            @Override
            public void updateVideoPosition(){
                position = player.getCurrentPosition();
                videoControllerView.setPosition(position);
                //videoControllerView.setBufferPercentage(bufferPercentage);
            }

            @Override
            public void restartVideo(){
                initPlayer();
                prepare();
            }

            @Override
            public void toggleScreen(boolean isPortrait){
                playerListener.toggleScreen(isPortrait);
                videoControllerView.showController(DEFAULT_CONTROLLER_SHOW_TIME);
            }

        });

        if(surfaceView != null){
            Log.i(TAG, "init: videoWidth = "+videoWidth+"videoHeight = "+videoHeight );

            holder = surfaceView.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    setSurfaceViewState(STATE_SURFACEVIEW_CREATE);
                    //drawCover(bitmap);

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    position = player.getCurrentPosition();

                }
            });
        }



    }
    public void initPlayer(){
        reset();

        player = new MediaPlayer();
        player.setScreenOnWhilePlaying(true);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setPlayerState(STATE_PLAYER_PREPARED);
                //在这里要停止视频加载动画
                player.setDisplay(holder);
                duration = player.getDuration();
                videoControllerView.setSeekBarMax(duration);
                play();

            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPlayerState(STATE_PLAYER_COMPLETE);
                videoControllerView.showFinishView();
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                setPlayerState(STATE_PLAYER_ERROR);
                return false;
            }
        });

        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                //播放器跳转完成，重新开启计时器
                //play();

                //position = mp.getCurrentPosition();
                Log.i(TAG, "onSeekComplete: =" + StringUtil.stringToTime(position));
                videoControllerView.showController(DEFAULT_CONTROLLER_SHOW_TIME);
                //play();


            }
        });

        //缓冲监听,暂时没用
        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    //开始加载视频
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        loadingView.setVisibility(View.VISIBLE);
                        break;
                    //加载视频结束
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        loadingView.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //Log.i(TAG, "onBufferingUpdate: buffer" + percent);
                //bufferPercentage = percent;
            }
        });
    }
    //播放
    public void play(){
        setPlayerState(STATE_PLAYER_PLAY);
        player.start();
    }

    //暂停
    public void pause(){
        setPlayerState(STATE_PLAYER_PAUSE);
        player.pause();
    }

    //停止播放
    public void stop(){
        if(player != null && player.isPlaying()){
            player.stop();
            player.release();
            player = null;
        }
    }

    public void prepare() {
        //
        ViewSizeUtil.changeSurfaceViewSize(getContext(),surfaceView,isPortrait,videoWidth,videoHeight);
        try {
            player.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        setPlayerState(STATE_PLAYER_PEEPARING);
    }

    public void reset(){
        if (player != null){
            player.reset();
            player.release();
            setPlayerState(STATE_PLAYER_IDLE);
        }
    }



    //
    public int getPlayerState() {
        return playerState;
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
        switch (playerState){
            case STATE_PLAYER_PEEPARING:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case STATE_PLAYER_PREPARED:
                loadingView.setVisibility(View.GONE);
                videoControllerView.showController(DEFAULT_CONTROLLER_SHOW_TIME);
                break;
            default:
                break;
        }
    }

    public int getSurfaceViewState() {
        return surfaceViewState;
    }

    public void setSurfaceViewState(int surfaceViewState) {
        surfaceViewState = surfaceViewState;
    }

    public boolean canSeek(){
        return player != null && getPlayerState() >= STATE_PLAYER_PREPARED;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }


    public void setSurfaceView(SurfaceView surfaceView) {
        surfaceView = surfaceView;
    }

    public int getViewState() {
        return viewState;
    }

    public void setViewState(int viewState) {
        viewState = viewState;
    }

    public void setVideoSize(int videoWidth,int videoHeight){
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return detector.onTouchEvent(event);
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    //
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp:s");
        if(playerState >= STATE_PLAYER_PREPARED){
            if(videoControllerView.isControllerBottomShow()){
                videoControllerView.hideController();
            }else {
                videoControllerView.showController(DEFAULT_CONTROLLER_SHOW_TIME);
                //videoControllerView.setPosition(player.getCurrentPosition());
                //videoControllerView.setBufferPosition(bufferPercentage);
            }
        }
        return true;
    }

    //轻击屏幕唤醒控制条
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public int getPosition() {
        return position;
    }

    public boolean isPlayFinish(int progress){
        return progress == duration;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换横竖屏布局
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            isPortrait = true;
        }else{
            //setVideoState(STATE_VIEW_HORIZONTAL);
            isPortrait = false;

        }
        ViewSizeUtil.changeSurfaceViewSize(getContext(),surfaceView,isPortrait,videoWidth,videoHeight);
        //getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        //getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
    }

    public void setPortrait(boolean portrait) {
        isPortrait = portrait;
    }

    public void registerNetBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netBroadcastReceiver = new NetworkBroadcastReceiver();
        context.registerReceiver(netBroadcastReceiver,intentFilter);
    }

    public void unregisterNetBroadcastReceiver(Context context){
        context.unregisterReceiver(netBroadcastReceiver);
    }
}
