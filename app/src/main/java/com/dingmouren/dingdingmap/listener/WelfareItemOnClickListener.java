package com.dingmouren.dingdingmap.listener;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by mouren on 2017/3/12.
 */
@FunctionalInterface
public interface WelfareItemOnClickListener {
    void onClick(ImageView view, String imgUrl, int position);
}
