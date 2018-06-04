package com.example.player.player;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.media.AudioManager;
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
import android.widget.RelativeLayout;


import com.example.player.R;
import com.example.player.player.listener.PlayerListener;
import com.example.player.player.listener.VideoControlListener;
import com.example.player.player.receiver.NetworkBroadcastReceiver;
import com.example.player.player.util.StringUtil;
import com.example.player.player.util.ViewSizeUtil;
import com.example.player.player.widget.VideoControllerView;
import com.example.player.player.widget.VideoErrorView;
import com.example.player.player.widget.VideoVolumeView;

import java.io.IOException;



/**
 * 使用MediaPlayer与SurfaceView实现的视频播放器
 * @author jiang
 * @date 2018/5/25
 *
 */
public class JxcPlayer extends RelativeLayout {
    private static final String TAG = "JxcPlayer";
    public static final int STATE_SURFACEVIEW_CREATE = -2;
    public static final int STATE_SURFACEVIEW_DESTROY = -1;
    public static final int STATE_PLAYER_IDLE = 0;
    public static final int STATE_PLAYER_PEEPARING = 1;
    public static final int STATE_PLAYER_ERROR = 2;
    public static final int STATE_PLAYER_SEEK = 3;
    public static final int STATE_PLAYER_STOP = 4;
    public static final int STATE_PLAYER_PREPARED = 5;
    public static final int STATE_PLAYER_COMPLETE = 6;
    public static final int STATE_PLAYER_PAUSE = 7;
    public static final int STATE_PLAYER_PLAY = 8;
    private boolean isPortrait;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private VideoControllerView videoControllerView;
    private View loadingView;
    private String videoPath;
    private int playerState;
    private int position;
    private int duration;
    private String cover;
    private Canvas canvas;
    private int videoWidth;
    private int videoHeight;
    private boolean isInBackground;
    private boolean isPause;
    private PlayerListener playerListener;
    private NetworkBroadcastReceiver netBroadcastReceiver;
    private int surfaceViewState;

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
    }


    public void init(){
        initPlayer();
        createSurface();
        //videoControllerView = new VideoControllerView(getContext());
        videoControllerView.setCover(cover);
        videoControllerView.setPortrait(isPortrait);
        videoControllerView.init();
        setPlayerState(STATE_PLAYER_IDLE);

        videoControllerView.setOnVideoControllerView(new VideoControlListener() {
            @Override
            public void startVideo() {
                Log.i(TAG, "startVideo: ");
                if(playerState == STATE_PLAYER_PREPARED || playerState == STATE_PLAYER_PAUSE){
                    play();

                }
            }

            @Override
            public void pauseVideo() {
                Log.i(TAG, "pauseVideo: ");
                if(playerState == STATE_PLAYER_PLAY){
                    pause();
                    isPause = true;
                }

            }

            @Override
            public void stopVideo() {
                Log.i(TAG, "stopVideo: ");
                if(playerState == STATE_PLAYER_PLAY){
                    stop();
                }
            }

            @Override
            public void prepareVideo() {
                Log.i(TAG, "prepareVideo: ");
                prepare();
                playerListener.isPlayerPrepare();
            }

            @Override
            public void seekVideo(int progress) {
                //if(canSeek()){
                //播放器跳转
                Log.i(TAG, "seekVideo: seekBar = " + StringUtil.stringToTime(progress));
                   // player.seekTo(progress);
                seekTo(progress);

            }
            @Override
            public void updateVideoPosition(){
               // Log.i(TAG, "updateVideoPosition: ");
                if(playerState == STATE_PLAYER_PLAY || playerState == STATE_PLAYER_PAUSE){
                    position = player.getCurrentPosition();
                    videoControllerView.setPosition(position);
                }

                //videoControllerView.setBufferPercentage(bufferPercentage);
            }

            @Override
            public void restartVideo(){
                Log.i(TAG, "restartVideo: ");
                restart();
            }

            @Override
            public void toggleScreen(boolean isPortrait){
                Log.i(TAG, "toggleScreen: ");
                playerListener.toggleScreen(isPortrait);
                videoControllerView.showControllerBottom(VideoControllerView.DEFAULT_CONTROLLER_SHOW_TIME);
            }

            @Override
            public void controllerBottom(){
                Log.i(TAG, "controllerBottom: ");
                if(playerState == STATE_PLAYER_PREPARED || playerState == STATE_PLAYER_PLAY || playerState == STATE_PLAYER_PAUSE){
                    videoControllerView.controllerBottom();
                }
            }

            @Override
            public int getPlayerState() {
                return playerState;
            }
        });



        registerNetBroadcastReceiver(getContext());

    }

    /**
     * 播放器初始化
     */
    public void initPlayer(){
        reset();

        player = new MediaPlayer();
        player.setScreenOnWhilePlaying(true);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //if(playerState == STATE_PLAYER_PEEPARING){
                    setPlayerState(STATE_PLAYER_PREPARED);


                    Log.i(TAG, "onPrepared:getDuration getDuration getDurationgetDuration ");
                    duration = player.getDuration();
                    if(holder != null){
                        player.setDisplay(holder);
                    }
                    videoControllerView.setSeekBarMax(duration);
                    if(!isPause){
                        play();
                    }
                //}

            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion: 2");
                if(playerState != STATE_PLAYER_ERROR && surfaceViewState != STATE_SURFACEVIEW_DESTROY){
                    setPlayerState(STATE_PLAYER_COMPLETE);
                    videoControllerView.showFinishView();
                }
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onError: what=" + what +"extra="+extra);
                setPlayerState(STATE_PLAYER_ERROR);
                if(surfaceViewState != STATE_SURFACEVIEW_DESTROY){

                    loadingView.setVisibility(GONE);
                    videoControllerView.showError();
                }

                return false;
            }
        });

        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                //播放器跳转完成，重新开启计时器
                //play();

                //position = mp.getCurrentPosition();
                //position = 0;
                Log.i(TAG, "onSeekComplete: =" + StringUtil.stringToTime(position));
                videoControllerView.showControllerBottom(VideoControllerView.DEFAULT_CONTROLLER_SHOW_TIME);
                //play();
                setPlayerState(STATE_PLAYER_PLAY);


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

    /**
     * 创建surfaceView
     */
    public void createSurface(){
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
                    //if(holder != null){
                        player.setDisplay(holder);
                   // }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                    //activity切到后台之后会调用
                   // if(playerState == STATE_PLAYER_PLAY){
                        position = player.getCurrentPosition();
                        Log.i(TAG, "surfaceDestroyed: position=" + position);
                        //在此释放了player
                   // }
                    Log.i(TAG, "surfaceDestroyed: position=" + position);
                    setSurfaceViewState(STATE_SURFACEVIEW_DESTROY);
                }
            });
        }
    }

    /**
     * 播放视频相关操作
     */
    public void play(){
        setPlayerState(STATE_PLAYER_PLAY);
        isPause = false;
        if(position != 0 && position != duration ){
            player.seekTo(position);

        }
        player.start();
    }

    public void pause(){
        setPlayerState(STATE_PLAYER_PAUSE);
        position = player.getCurrentPosition();
        player.pause();
    }

    public void stop(){
        if(player != null){
            Log.i(TAG, "stop: ");
            player.stop();
            player.release();

            player = null;
        }
        setPlayerState(STATE_PLAYER_STOP);
    }

    public void seekTo(int position){
        player.seekTo(position);
        if(playerState == STATE_PLAYER_PAUSE){
            player.start();
            videoControllerView.changePlayButtonView(true);
        }
        setPlayerState(STATE_PLAYER_SEEK);
    }

    public void prepare() {
        //
        ViewSizeUtil.changeSurfaceViewSize(getContext(),surfaceView,isPortrait,videoWidth,videoHeight);
        try {
            player.setDataSource(videoPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
        muteAudioFocus(getContext().getApplicationContext(),true);
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

    public void restart(){
        initPlayer();
        if(surfaceViewState == STATE_SURFACEVIEW_DESTROY){
            createSurface();
        }
        prepare();
    }

    /**
     * 根据重力感应切换相应布局
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换横竖屏布局
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            isPortrait = true;
        }else{
            isPortrait = false;

        }
        ViewSizeUtil.changeSurfaceViewSize(getContext(),surfaceView,isPortrait,videoWidth,videoHeight);
    }



    /**
     * 注册网络广播
     * @param context
     */
    public void registerNetBroadcastReceiver(Context context){
        if(netBroadcastReceiver == null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetworkBroadcastReceiver();
            netBroadcastReceiver.setVideoControllerView(videoControllerView);
            context.registerReceiver(netBroadcastReceiver,intentFilter);
        }

    }

    /**
     * 注销网络广播
     * @param context
     */
    public void unregisterNetBroadcastReceiver(Context context){
        if(netBroadcastReceiver != null){
            context.unregisterReceiver(netBroadcastReceiver);
        }
        netBroadcastReceiver = null;
    }

    /**
     * 生命周期相关
     */
    public void onResume(){
        registerNetBroadcastReceiver(getContext());
        if(isInBackground && playerState != STATE_PLAYER_COMPLETE){
            restart();
        }
        if(playerState == STATE_PLAYER_COMPLETE){
            videoControllerView.showFinishView();
        }
        Log.i(TAG, "onResume: position = "+position);
        isInBackground = false;
    }

    public void onStop(){
        if(playerState == STATE_PLAYER_COMPLETE){
            videoControllerView.hideFinishView();
        }else{
            Log.i(TAG, "onStop: ");
            stop();
            setPlayerState(STATE_PLAYER_IDLE);
        }

        isInBackground = true;
        muteAudioFocus(getContext().getApplicationContext(),false);
        unregisterNetBroadcastReceiver(getContext());
    }
    public void onDestroy(){
        if(player != null){
            player.stop();
            player.release();
        }
        player = null;
        unregisterNetBroadcastReceiver(getContext());
        videoControllerView.onDestroy();
        videoControllerView = null;
    }

    /**
     * 变量设置
     */
    public void setPlayerState(int playerState) {
        Log.i(TAG, "setPlayerState: " + playerState);
        this.playerState = playerState;
        switch (playerState){
            case STATE_PLAYER_PEEPARING:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case STATE_PLAYER_PREPARED:
                loadingView.setVisibility(View.GONE);
                videoControllerView.showControllerBottom(VideoControllerView.DEFAULT_CONTROLLER_SHOW_TIME);
                break;
            case STATE_PLAYER_PAUSE:
                position = player.getCurrentPosition();
                break;
            case STATE_PLAYER_COMPLETE:
                position = duration;
                break;
            default:
                break;
        }
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setVideoSize(int videoWidth,int videoHeight){
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    public void setPortrait(boolean portrait) {
        isPortrait = portrait;
    }

    public void setSurfaceViewState(int surfaceViewState){
        this.surfaceViewState = surfaceViewState;
    }

    /**
     * 请求音频焦点
     * @param context
     * @param bMute
     * @return
     */
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int result = -1;
        if(bMute){
            result = audioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }else{
            result = audioManager.abandonAudioFocus(null);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }
}
