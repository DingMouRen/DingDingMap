package com.dingmouren.dingdingmap.util;

import android.util.TypedValue;

import com.dingmouren.dingdingmap.MyApplication;

/**
 * Created by dingmouren on 2017/3/4.
 */

public class Dp2Sp2PxUtil {
    /**
     * 将 px 转换为 dip 或 dp， 保证尺寸大小不变
     */
    public static int px2dip( float pxValue) {

        /* density 是屏幕比例因子， 以 160dpi（1px = 1dp） 为标准 density 值为1，320dpi（2px = 1dp） 中 density 值为 2（320/160） */
        final float scale = MyApplication.applicationContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 將 dip 或 dp 转换为 px, 保证尺寸大小不变
     *
     */
    public static int dip2px(  float dipValue) {
        final float scale = MyApplication.applicationContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将 px 转换为 sp， 保证尺寸大小不变
     */
    public static int px2sp(  float pxValue) {

        /* scaledDensity，字体的比例因子，类似 density， 会根据用户偏好返回不同的值*/
        final float fontScale = MyApplication.applicationContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将 sp 转换为 px， 保证尺寸大小不变
     */
    public static int sp2px(  float pxValue) {
        final float fontScale = MyApplication.applicationContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue * fontScale + 0.5f);
    }

    /**
     * 使用系统工具类 TypedValue 帮助把 数值 转换到 px
     */
    public static int dp2px(  int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, MyApplication.applicationContext.getResources().getDisplayMetrics());
    }
}
