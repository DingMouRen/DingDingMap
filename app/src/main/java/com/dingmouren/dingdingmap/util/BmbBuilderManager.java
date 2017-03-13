package com.dingmouren.dingdingmap.util;

import android.graphics.Color;

import com.dingmouren.dingdingmap.R;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;

/**
 * Created by dingmouren on 2017/3/8.
 */

public class BmbBuilderManager {
    private static int[] imageResources = new int[]{
            R.drawable.money,
            R.drawable.route,
            R.drawable.share,
            R.drawable.girl,
            R.drawable.offline_map,
            R.drawable.about,
    };
    private static String[] strResources = new String[]{"赚钱","路线","分享","福利","离线","关于"};
    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    public static SimpleCircleButton.Builder getSimpleCircleButtonBuilder() {
        return new SimpleCircleButton.Builder()
                .normalImageRes(getImageResource());
    }
}
