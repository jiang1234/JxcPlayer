package com.example.gzs11543.jxcplayer.net;

import com.example.gzs11543.jxcplayer.bean.VideoBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public interface VideoApiServer {
    /**
     * return video info
     * @return video info
     */
    @GET("waterfall_list? client=android&page=1&size=15&videoid=5a0cfb2c7a2059547de69ac4")
    Observable<VideoBean> getVideoList();

    @GET("waterfall_list?client=android")
    Observable<VideoBean> getMoreVideoList(@Query("page") String page);
}
