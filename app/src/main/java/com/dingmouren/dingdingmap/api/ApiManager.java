package com.dingmouren.dingdingmap.api;

import com.dingmouren.dingdingmap.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mouren on 2017/3/12.
 */

public class ApiManager {
    private static final int READ_TIME_OUT = 3;
    private static final int CONNECT_TIME_OUT = 3;
    private Api mApiService;
    private ApiManager(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT,TimeUnit.SECONDS)
                .build();
        Retrofit retrofit1 = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Constant.GANK_BASE_URL)
                .build();
        mApiService = retrofit1.create(Api.class);
    }
    /**
     * 单例对象持有者
     */
    private static class SingletonHolder{
        private static final ApiManager INSTANCE = new ApiManager();
    }

    /**
     * 获取ApiManager单例对象
     * @return
     */
    public static ApiManager getApiInstance(){
        return SingletonHolder.INSTANCE;
    }

    public Api getApiService(){
        return mApiService;
    }
}
