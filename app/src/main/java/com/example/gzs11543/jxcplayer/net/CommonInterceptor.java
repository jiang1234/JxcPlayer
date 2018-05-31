package com.example.gzs11543.jxcplayer.net;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CommonInterceptor implements Interceptor {
    private final String size = "15";
    private final String videoId = "5a0cfb2c7a2059547de69ac4";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        //添加固定参数
        HttpUrl.Builder builder = oldRequest.url()
                .newBuilder()
                .addQueryParameter("size",size)
                .addQueryParameter("videoId",videoId);

        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(),oldRequest.body())
                .url(builder.build())
                .build();

        return chain.proceed(newRequest);
    }
}
