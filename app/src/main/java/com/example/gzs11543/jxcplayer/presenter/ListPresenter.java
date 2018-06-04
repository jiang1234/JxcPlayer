package com.example.gzs11543.jxcplayer.presenter;

import android.util.Log;

import com.example.gzs11543.jxcplayer.ListActivity;
import com.example.gzs11543.jxcplayer.base.BaseOberver;
import com.example.gzs11543.jxcplayer.base.BasePresenter;
import com.example.gzs11543.jxcplayer.bean.VideoBean;
import com.example.gzs11543.jxcplayer.net.VideoApi;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public class ListPresenter extends BasePresenter<ListActivity>{
    private final static String TAG = "ListPresenter";
    private VideoApi mVideoApi;

    public ListPresenter(VideoApi videoApi){
        this.mVideoApi = videoApi;
    }

    public void getVideoList(String pageId){
        mVideoApi.getMoreVideoInfo(pageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(view.<VideoBean>bindToLifecycle())
                .subscribe(new SingleObserver<VideoBean>(){

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(VideoBean videoBean) {
                        if(view != null){
                            view.loadListInfo(videoBean);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG,"NotFound 404");
                    }
                });
    }
}
