package com.dingmouren.dingdingmap.event;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by dingmouren on 2017/3/4.
 */

public class EventPoint {
    private String title;
    private LatLonPoint latLonPoint;
    private int tag;
    private String cityCode;
    public EventPoint(String title, LatLonPoint latLonPoint, int tag,String cityCode) {
        this.title = title;
        this.latLonPoint = latLonPoint;
        this.tag = tag;
        this.cityCode = cityCode;
    }

    public String getTitle() {
        return title;
    }

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }

    public int getTag() {
        return tag;
    }

    public String getCityCode() {
        return cityCode;
    }
}
