package com.dingmouren.dingdingmap.listener;

import android.view.View;

/**
 * Created by mouren on 2017/3/12.
 */
@FunctionalInterface
public interface WelfareItemOnClickListener {
    void onClick(View view,String imgUrl,int position);
}
