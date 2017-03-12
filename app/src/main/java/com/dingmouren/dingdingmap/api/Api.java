package com.dingmouren.dingdingmap.api;

import com.dingmouren.dingdingmap.bean.GankResult;
import com.dingmouren.dingdingmap.bean.GankResultWelfare;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by mouren on 2017/3/12.
 */

public interface Api {
    //获取图片
    @GET("data/福利/16/{page}")
    Observable<GankResult<List<GankResultWelfare>>> getGirlPics(@Path("page") int page);
}
