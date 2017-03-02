package com.dingmouren.dingdingmap.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.BusSegmentListAdapter;
import com.dingmouren.dingdingmap.util.AMapUtil;
import com.dingmouren.dingdingmap.util.BusRouteOverlay;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/2.
 */

public class BusRouteDetailActivity extends BaseActivity implements AMap.OnMapLoadedListener,AMap.OnMapClickListener
                ,AMap.InfoWindowAdapter,AMap.OnInfoWindowClickListener,AMap.OnMarkerClickListener{
    private static final String TAG = BusRouteDetailActivity.class.getName();
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.firstline) TextView mFirstLine;
    @BindView(R.id.listview) ListView mListView;
    @BindView(R.id.mapview)   MapView mMapView;
    private BusPath mBuspath;
    private BusRouteResult mBusRouteResult;
    private AMap mMap;
    private BusRouteOverlay mBusrouteOverlay;
    private BusSegmentListAdapter mBusSegmentListAdapter;
    private String duration;
    private String distinct;

    public static void newInstance(Activity activity, BusPath busPath, BusRouteResult busRouteResult){
        Intent intent = new Intent(activity,BusRouteDetailActivity.class);
        intent.putExtra("bus_path",busPath);
        intent.putExtra("bus_result",busRouteResult);
        activity.startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_bus_route_detail;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (null != getIntent()){
            mBuspath = getIntent().getParcelableExtra("bus_path");
            mBusRouteResult = getIntent().getParcelableExtra("bus_result");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mToolbar.setTitle("公交路线详情");
        setSupportActionBar(mToolbar);

        mMapView.onCreate(savedInstanceState);
        if (null == mMap) mMap = mMapView.getMap();

        duration = AMapUtil.getFriendlyTime((int)mBuspath.getDuration());
        distinct = AMapUtil.getFriendlyLength((int)mBuspath.getDistance());
        mFirstLine.setText(duration+"("+distinct+")");


        mBusSegmentListAdapter = new BusSegmentListAdapter(MyApplication.applicationContext,mBuspath.getSteps());
        mListView.setAdapter(mBusSegmentListAdapter);
    }

    @Override
    public void initListener() {
        mMap.setOnMapLoadedListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mToolbar.setNavigationOnClickListener(v -> finish());
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mapview){
                    mListView.setVisibility(View.GONE);
                    mFirstLine.setVisibility(View.GONE);
                    mMapView.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mBusrouteOverlay = new BusRouteOverlay(BusRouteDetailActivity.this,mMap,mBuspath,mBusRouteResult.getStartPos(),mBusRouteResult.getTargetPos());
                    mBusrouteOverlay.removeFromMap();
                }
                return true;
            }
        });
    }

    @Override
    public void initData() {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bus_detail,menu);
        return true;
    }

    @Override//OnMapLoadedListener
    public void onMapLoaded() {
        if (mBusrouteOverlay != null) {
            mBusrouteOverlay.addToMap();
            mBusrouteOverlay.zoomToSpan();
        }
    }

    @Override//OnMapClickListener
    public void onMapClick(LatLng latLng) {

    }

    @Override//InfoWindowAdapter
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override//InfoWindowAdapter
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override//OnInfoWindowClickListener
    public void onInfoWindowClick(Marker marker) {

    }

    @Override//OnMarkerClickListener
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
