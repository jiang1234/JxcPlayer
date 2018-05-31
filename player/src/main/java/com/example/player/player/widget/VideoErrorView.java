package com.example.player.player.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.player.R;
import com.example.player.player.listener.VideoControlListener;
import com.example.player.player.receiver.NetworkBroadcastReceiver;

public class VideoErrorView extends LinearLayout {
    public final static int NO_ERROR = 0;
    public final static int ERROR_TYPE_NO_INTERNET = 1;
    public final static int ERROR_TYPE_MOBILE_INTERNET = 2;
    public final static int ERROR_TYPE_OTHER_ERROR = 3;
    private Button retryButton;
    private TextView errorTextView;
    private VideoControlListener videoControlListener;
    private int networkState;
    private View errorView;
    private int errorType;


    public VideoErrorView(Context context) {
        this(context,null);
    }

    public VideoErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.video_error_layout,this);
        retryButton = (Button)findViewById(R.id.retry);
        errorTextView = (TextView)findViewById(R.id.error_textview);
        errorView = (View)findViewById(R.id.error_view);

        retryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (errorType){
                    case ERROR_TYPE_NO_INTERNET:
                        if(networkState == NetworkBroadcastReceiver.NO_INTERNET){
                            Toast.makeText(getContext(),"当前无网络",Toast.LENGTH_SHORT).show();
                        }else if(networkState == NetworkBroadcastReceiver.INTERENT_STATE_WIFI){
                            videoControlListener.restartVideo();
                            changeErrorView(NO_ERROR);
                        }else if(networkState == NetworkBroadcastReceiver.INTERENT_STATE_MOBILE){
                            videoControlListener.startVideo();
                            changeErrorView(ERROR_TYPE_MOBILE_INTERNET);
                        }
                        break;
                    case ERROR_TYPE_OTHER_ERROR:
                        videoControlListener.restartVideo();
                        changeErrorView(NO_ERROR);
                        break;
                    case ERROR_TYPE_MOBILE_INTERNET:
                        videoControlListener.startVideo();
                        errorView.setVisibility(View.GONE);
                        break;
                    default:break;
                }

            }
        });
    }

    public void setNetworkState(int networkState) {
        this.networkState = networkState;
    }

    public void changeErrorView(int errorType){
        this.errorType = errorType;
        switch (errorType){
            case NO_ERROR:
                errorView.setVisibility(View.GONE);
                break;
            case ERROR_TYPE_NO_INTERNET:
                errorView.setVisibility(View.VISIBLE);
                errorTextView.setText("网络连接异常，请检查网络连接后重试");
                retryButton.setText("重试");

                break;
            case ERROR_TYPE_MOBILE_INTERNET:
                errorView.setVisibility(View.VISIBLE);
                errorTextView.setText("您正在使用移动网络，播放将产生流量费用");
                retryButton.setText("继续播放");
                break;
            case ERROR_TYPE_OTHER_ERROR:
                errorView.setVisibility(View.VISIBLE);
                errorTextView.setText("视频加载失败");
                retryButton.setText("重试");
                break;
            default:break;
        }
    }

    public int getErrorType() {
        return errorType;
    }

    public void setVideoControlListener(VideoControlListener videoControlListener) {
        this.videoControlListener = videoControlListener;
    }
}

