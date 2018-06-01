package com.example.gzs11543.jxcplayer.net;



import com.example.gzs11543.jxcplayer.bean.VideoBean;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public class VideoApi {
    private static class Holder{
        private static final VideoApi instance = new VideoApi();
    }

    private VideoApi(){}

    public static VideoApi getInstance(){
        return Holder.instance;
    }

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new CommonInterceptor())
            .build();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://mdiscover.cc.163.com/discover/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build();

    private VideoApiServer service = retrofit.create(VideoApiServer.class);

    public Observable<VideoBean> getVideoInfo(){
        return service.getVideoList();
    }

    public Observable<VideoBean> getMoreVideoInfo(String pageId){
        return service.getMoreVideoList(pageId);
    }





}
