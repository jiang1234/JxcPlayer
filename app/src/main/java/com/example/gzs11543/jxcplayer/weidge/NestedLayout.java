package com.example.gzs11543.jxcplayer.weidge;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gzs11543.jxcplayer.util.RecyclerViewUtil;

import java.util.jar.Attributes;

public class NestedLayout extends LinearLayout {

    private static final String TAG = "NestedLayout";
    private int mLastX;
    private int mLastY;
    private int mStart;
    private int mEnd;
    public NestedLayout(Context context) {
        this(context,null);
    }

    public NestedLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public NestedLayout(Context context, AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int)ev.getX();
        int y = (int)ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX  = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //垂直滑动
                Log.i(TAG, "onInterceptTouchEvent: true"+ev.getAction());
                if(Math.abs(mLastX - x) < Math.abs(mLastY - y)){
                    //向下滑动且数据加载完且滑倒底部
                    Log.i(TAG, "onInterceptTouchEvent: true"+ev.getAction());
                    Log.i(TAG, "onInterceptTouchEvent isEnd:"+ RecyclerViewUtil.isEnd);
                    Log.i(TAG, "onInterceptTouchEvent isBottomItem: "+RecyclerViewUtil.isBottomItem);
                    if(y - mLastY < 0 && RecyclerViewUtil.isEnd && RecyclerViewUtil.isBottomItem){
                        Log.i(TAG, "onInterceptTouchEvent: true3");
                        return true;
                    }
                }
                break;
            default:
                break;
        }
        Log.i(TAG, "onInterceptTouchEvent: false"+ev.getAction());
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStart = getScrollY();
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                scrollTo(0,(int)(-(y - mLastY)*0.4));
                mEnd = getScrollY();
                break;
            case MotionEvent.ACTION_UP:

                scrollBy(0,mStart - mEnd);
                RecyclerViewUtil.setIsBottomItem(false);

                break;
            default:
                break;
        }
        return true;
    }
}
