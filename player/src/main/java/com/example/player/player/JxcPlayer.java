package com.example.player.player;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
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

import java.io.IOException;



/**
 * 使用MediaPlayer与SurfaceView实现的视频播放器
 * @author jiang
 * @date 2018/5/25
 *
 */
public class JxcPlayer extends RelativeLayout implements GestureDetector.OnGestureListener{
    private static final String TAG = "JxcPlayer";

    private static final int STATE_VIEW_VERTICAL = 0;
    private static final int STATE_VIEW_HORIZONTAL = 1;
    private static final int STATE_SURFACEVIEW_CREATE = 2;
    private static final int STATE_SURFACEVIEW_DESTROY = 3;
    private static final int STATE_PLAYER_IDLE = 4;
    private static final int STATE_PLAYER_PEEPARING = 5;
    private static final int STATE_PLAYER_ERROR = 6;
    private static final int STATE_PLAYER_SEEK = 7;
    private static final int STATE_PLAYER_STOP = 8;
    private static final int STATE_PLAYER_PREPARED = 9;
    private static final int STATE_PLAYER_COMPLETE = 10;


    private static final int STATE_PLAYER_PAUSE = 11;
    private static final int STATE_PLAYER_PLAY = 12;
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
    private boolean isInBackground;
    private boolean isPause;
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
        createSurface();
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
                    isPause = true;
                }

            }

            @Override
            public void stopVideo() {
                if(playerState == STATE_PLAYER_PLAY){
                    stop();
                }
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
                   // player.seekTo(progress);
                seekTo(progress);

            }
            @Override
            public void updateVideoPosition(){
                if(playerState == STATE_PLAYER_PLAY || playerState == STATE_PLAYER_PAUSE){
                    position = player.getCurrentPosition();
                    videoControllerView.setPosition(position);
                }

                //videoControllerView.setBufferPercentage(bufferPercentage);
            }

            @Override
            public void restartVideo(){
                restart();
            }

            @Override
            public void toggleScreen(boolean isPortrait){
                playerListener.toggleScreen(isPortrait);
                videoControllerView.showControllerBottom(VideoControllerView.DEFAULT_CONTROLLER_SHOW_TIME);
            }

        });



        registerNetBroadcastReceiver(getContext());

    }
    public void initPlayer(){
        reset();

        player = new MediaPlayer();
        player.setScreenOnWhilePlaying(true);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setPlayerState(STATE_PLAYER_PREPARED);
                player.setDisplay(holder);
                duration = player.getDuration();
                videoControllerView.setSeekBarMax(duration);
                if(!isPause){
                    play();
                }
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion: 2");
                if(playerState != STATE_PLAYER_ERROR){
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
                loadingView.setVisibility(GONE);
                videoControllerView.showError();
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

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                    //activity切到后台之后会调用
                    if(playerState == STATE_PLAYER_PLAY){
                        position = player.getCurrentPosition();
                        Log.i(TAG, "surfaceDestroyed: position=" + position);
                        stop();//在此释放了player
                    }
                    Log.i(TAG, "surfaceDestroyed: position=" + position);
                    setSurfaceViewState(STATE_SURFACEVIEW_DESTROY);
                }
            });
        }
    }
    //播放
    public void play(){
        setPlayerState(STATE_PLAYER_PLAY);
        isPause = false;
        if(position != 0 && position != duration){
            player.seekTo(position);
        }
        player.start();
    }

    //暂停
    public void pause(){
        setPlayerState(STATE_PLAYER_PAUSE);
        position = player.getCurrentPosition();
        player.pause();
    }

    //停止播放
    public void stop(){
        if(player != null && player.isPlaying()){
            player.stop();
            player.release();

            player = null;
        }
//        surfaceView.setVisibility(View.GONE);
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
        player.prepareAsync();
        setPlayerState(STATE_PLAYER_PEEPARING);
    }

    public void reset(){
        if (player != null){
            player.reset();
            //player.release();
            setPlayerState(STATE_PLAYER_IDLE);
        }
    }

    public void restart(){
        initPlayer();
        prepare();
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
                videoControllerView.hideControllerBottom();
            }else {
                videoControllerView.showControllerBottom(VideoControllerView.DEFAULT_CONTROLLER_SHOW_TIME);
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
        if(netBroadcastReceiver == null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetworkBroadcastReceiver();
            netBroadcastReceiver.setVideoControllerView(videoControllerView);
            context.registerReceiver(netBroadcastReceiver,intentFilter);
        }

    }

    public void unregisterNetBroadcastReceiver(Context context){
        if(netBroadcastReceiver != null){
            context.unregisterReceiver(netBroadcastReceiver);
        }
        netBroadcastReceiver = null;
    }

    public void onResume(){
        registerNetBroadcastReceiver(getContext());
        if(isInBackground && videoControllerView.getErrorType() == VideoErrorView.NO_ERROR && (playerState == STATE_PLAYER_STOP ||playerState == STATE_PLAYER_PAUSE)){
            //createSurface();
            restart();
        }
        Log.i(TAG, "onResume: position = "+position);
        isInBackground = false;
    }

    public void onStop(){
        isInBackground = true;
        unregisterNetBroadcastReceiver(getContext());
    }
    public void onDestroy(){
        player.stop();
        player.release();
        unregisterNetBroadcastReceiver(getContext());
    }
}
