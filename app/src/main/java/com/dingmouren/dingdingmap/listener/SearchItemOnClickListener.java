package com.dingmouren.dingdingmap.listener;

import android.view.View;

import com.amap.api.services.core.PoiItem;


/**
 * Created by mouren on 2017/2/28.
 */
@FunctionalInterface
public interface SearchItemOnClickListener {
    void onClick(View view, PoiItem poiItem, int position);
}
