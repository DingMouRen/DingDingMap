package com.dingmouren.dingdingmap.ui.route_plan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/1.
 */

public class RoutePlanActivity extends BaseActivity implements AMap.OnMapClickListener,AMap.OnMarkerClickListener
                        ,AMap.OnInfoWindowClickListener,AMap.InfoWindowAdapter,RouteSearch.OnRouteSearchListener{
    private static final String TAG = RoutePlanActivity.class.getName();
    @BindView(R.id.edit_start)  MaterialEditText mEditStart;
    @BindView(R.id.edit_end) MaterialEditText mEditEnd;
    @BindView(R.id.img_back) ImageView mImgBack;
    @BindView(R.id.img_return) ImageView mImgReturn;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.mapview)  MapView mMapView;
    @BindView(R.id.bottom_info)  RelativeLayout mBottomInfo;
    @BindView(R.id.recycler)  RecyclerView mRecycler;
    @BindView(R.id.firstline)   TextView mFirstLinea;
    @BindView(R.id.secondline) TextView mSeconLine;
    @BindView(R.id.detail)   LinearLayout mDetail;

    private AMap mAMap;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint;//起点
    private LatLonPoint mEndPoint;//终点
    private String mCurrentCityName = "聊城";
    private final int ROUTE_TYPE_DRIVE = 1;
    private final int ROUTE_TYPE_BUS = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private ProgressDialog progDialog = null;//搜索时进度条

    public static String[] ways = new String[]{"驾车","公交","步行","骑行"};
    @Override
    public int setLayoutId() {
        return R.layout.activity_route_plan;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        if (null == mAMap) mAMap = mMapView.getMap();
        mRouteSearch = new RouteSearch(this);

        for (int i = 0; i < ways.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(ways[i]));
        }
        mTabLayout.setScrollPosition(1,0,true);//滑动到指定为tab
    }

    @Override
    public void initListener() {
        if (null != mRouteSearch) mRouteSearch.setRouteSearchListener(this);
        mAMap.setOnMapClickListener(RoutePlanActivity.this);
        mAMap.setOnMarkerClickListener(RoutePlanActivity.this);
        mAMap.setOnInfoWindowClickListener(RoutePlanActivity.this);
        mAMap.setInfoWindowAdapter(RoutePlanActivity.this);

        mImgBack.setOnClickListener(v -> finish());
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override//OnMapClickListener
    public void onMapClick(LatLng latLng) {

    }

    @Override//OnMarkerClickListener
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override//OnInfoWindowClickListener
    public void onInfoWindowClick(Marker marker) {

    }

    @Override//InfoWindowAdapter-1
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override//InfoWindowAdapter-2
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
