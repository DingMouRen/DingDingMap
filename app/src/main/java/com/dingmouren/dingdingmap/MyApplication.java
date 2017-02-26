package com.dingmouren.dingdingmap;

import android.app.Application;
import android.content.Context;

import com.jiongbull.jlog.*;
import com.jiongbull.jlog.BuildConfig;

/**
 * Created by dingmouren on 2017/2/26.
 */

public class MyApplication extends Application {
    public static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        JLog.init(this).setDebug(BuildConfig.DEBUG);//初始化Jlog

    }
}
