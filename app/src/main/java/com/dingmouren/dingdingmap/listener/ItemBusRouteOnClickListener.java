package com.dingmouren.dingdingmap.listener;

import android.view.View;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

/**
 * Created by dingmouren on 2017/3/2.
 */

public interface ItemBusRouteOnClickListener {
    void onClick(View view, BusPath busPath, BusRouteResult busRouteResult,int position);
}
