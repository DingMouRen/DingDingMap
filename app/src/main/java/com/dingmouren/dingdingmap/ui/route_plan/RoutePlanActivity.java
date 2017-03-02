package com.dingmouren.dingdingmap.ui.route_plan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.RoutePlanBusAdapter;
import com.dingmouren.dingdingmap.ui.detail.BusRouteDetailActivity;
import com.dingmouren.dingdingmap.util.AMapUtil;
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
    @BindView(R.id.route_map)  MapView mMapView;
    @BindView(R.id.bottom_info)  RelativeLayout mBottomInfo;
    @BindView(R.id.recycler)  RecyclerView mRecycler;
    @BindView(R.id.firstline)   TextView mFirstLinea;
    @BindView(R.id.secondline) TextView mSeconLine;
    @BindView(R.id.detail)   LinearLayout mDetail;
    @BindView(R.id.progressbar) ProgressBar mProgressBar;

    private AMap mAMap;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private LatLonPoint mStartPoint_bus = new LatLonPoint(40.818311, 111.670801);//起点，111.670801,40.818311
    private LatLonPoint mEndPoint_bus = new LatLonPoint(44.433942, 125.184449);//终点，
    private String mCurrentCityName = "北京";
    private final int ROUTE_TYPE_DRIVE = 1;
    private final int ROUTE_TYPE_BUS = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private ProgressDialog progDialog = null;//搜索时进度条
    private RoutePlanBusAdapter mBusAdapter;

    public static String[] ways = new String[]{"驾车","公交","步行","骑行"};

    public static void newInstance(Activity activity,LatLonPoint startPoint,LatLonPoint endPoint){
        Intent intent = new Intent(activity,RoutePlanActivity.class);
        activity.startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_route_plan;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        if (null == mAMap) mAMap = mMapView.getMap();
        mRouteSearch = new RouteSearch(this);
        mAMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(mStartPoint)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
        mAMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(mEndPoint)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.end)));

        for (int i = 0; i < ways.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(ways[i]));
        }
        mTabLayout.setScrollPosition(1,0,true);//滑动到指定为tab
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
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
                switch (tab.getPosition()){
                    case 0:
                        mMapView.setVisibility(View.VISIBLE);
                        mRecycler.setVisibility(View.GONE);
                        break;
                    case 1:
                        searchBusRoute();
                        break;
                    case 2:
                        mMapView.setVisibility(View.VISIBLE);
                        mRecycler.setVisibility(View.GONE);
                        break;
                    case 3:
                        break;
                }
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
        searchBusRoute();//初始化公交车路线数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /**
     * 公交路线搜索
     */
    private void searchBusRoute(){
        mMapView.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        mBottomInfo.setVisibility(View.GONE);
        searchRouteResult(1,RouteSearch.BUS_DEFAULT);
    }

    /**
     * 开始搜索路线
     * @param routeType
     * @param mode
     */
    private void searchRouteResult(int routeType, int mode){
        if (null == mStartPoint){
            Toast.makeText(MyApplication.applicationContext,"起点未设置",Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == mEndPoint){
            Toast.makeText(MyApplication.applicationContext,"终点未设置",Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint,mEndPoint);
        switch (routeType){
            case 0:
                //第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo,mode,null,null,"");
                mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
                break;
            case 1:
                RouteSearch.BusRouteQuery busRouteQuery = new RouteSearch.BusRouteQuery(fromAndTo,mode,mCurrentCityName,0);//0表示不计算夜班车
                mRouteSearch.calculateBusRouteAsyn(busRouteQuery);
                break;
            case 2:
                RouteSearch.WalkRouteQuery walkRouteQuery = new RouteSearch.WalkRouteQuery(fromAndTo,mode);
                mRouteSearch.calculateWalkRouteAsyn(walkRouteQuery);
                break;
            case 3:
                break;
        }
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

    @Override//公交路线搜索结果方法回调
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        mProgressBar.setVisibility(View.GONE);
        mBottomInfo.setVisibility(View.GONE);
        mAMap.clear();//清空地图上的覆盖物
        if (rCode == AMapException.CODE_AMAP_SUCCESS){
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                   mBusAdapter = new RoutePlanBusAdapter(mBusRouteResult);
                    //公交车详线路情
                    mBusAdapter.setOnItemClickListener((view, busPath, busRouteResult, position) -> {
                        BusRouteDetailActivity.newInstance(RoutePlanActivity.this,busPath,busRouteResult);
                    });
                    mRecycler.setAdapter(mBusAdapter);
                } else if (result != null && result.getPaths() == null) {
                    Toast.makeText(MyApplication.applicationContext,"~~(>_<)~~ 没有搜索到相关数据",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MyApplication.applicationContext,"~~(>_<)~~ 没有搜索到相关数据",Toast.LENGTH_SHORT).show();

            }
        }else {


        }
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
