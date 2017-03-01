package com.dingmouren.dingdingmap.ui.routedetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.home.MainActivity;
import com.dingmouren.dingdingmap.ui.search.SearchActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by mouren on 2017/2/28.
 */

public class RouteDetailActivity extends BaseActivity implements LocationSource,AMapLocationListener {
    private static final String TAG = RouteDetailActivity.class.getName();
    private static final String DATA = "data";
    @BindView(R.id.mapview) MapView mMapView;
    @BindView(R.id.search_bar) MaterialSearchBar mSearchBar;
    @BindView(R.id.tv_name) TextView mName;
    @BindView(R.id.tv_distance) TextView mDistance;
    @BindView(R.id.tv_address) TextView mAddress;
    @BindView(R.id.fab_location) FloatingActionButton mFabLocation;
    @BindView(R.id.fab_to_where) FloatingActionButton mFabToWhere;
    private AMap mAMap;//地图控制类
    private PoiItem mPoiItem;
    private UiSettings mUiSettings;//操作控件类
    private OnLocationChangedListener mLocationChangedListener;//定位回调监听
    private AMapLocationClient mLocationClient;//AMapLocationClient类对象
    private AMapLocationClientOption mLocationOption;//定位参数对象
    private double mLatitude;//纬度
    private double mLongitude;//经度
    private Marker mLocationMarker;
    private String mMyLocationAdress;

    public static void newInstance(Activity activity, PoiItem poiItem){
        Intent intent = new Intent(activity,RouteDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA, (Parcelable) poiItem);
        intent.putExtras(bundle);
        ((SearchActivity)activity).startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_route_detail;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        mPoiItem = (PoiItem) getIntent().getExtras().getParcelable(DATA);

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);//创建地图
        if (null == mAMap) mAMap = mMapView.getMap();//获取地图控制类
        if (null == mUiSettings && null != mAMap){
            mUiSettings = mAMap.getUiSettings();//获取操作控件类
            mUiSettings.setScaleControlsEnabled(false);//是否显示比例尺控件
            mUiSettings.setZoomControlsEnabled(false);//是否显示缩放按钮
            mUiSettings.setLogoLeftMargin(getWindowManager().getDefaultDisplay().getWidth());//隐藏高德地图的Logo
        }
        mAMap.showMapText(true);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);

        //定位
        mAMap.setLocationSource(this);//设置定位监听
        mUiSettings.setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        mAMap.clear();
        MarkerOptions markerOptions =  new MarkerOptions()
                .position(new LatLng(mPoiItem.getLatLonPoint().getLatitude(),mPoiItem.getLatLonPoint().getLongitude()))
                .title(mPoiItem.getTel()).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.poi)));
        mAMap.addMarker(markerOptions);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mPoiItem.getLatLonPoint().getLatitude(),mPoiItem.getLatLonPoint().getLongitude()),12));//级别在3~21之间，数字越大，地图越详细，地图缩放级别
        //显示所选位置的信息
        if (null != mPoiItem){
            mName.setText(mPoiItem.getTitle());
            mAddress.setText(mPoiItem.getProvinceName()+mPoiItem.getCityName()+mPoiItem.getAdName()+mPoiItem.getSnippet());
        }
    }



    @Override
    public void initListener() {
        mSearchBar.enableSearch();
        mSearchBar.setHint(mPoiItem == null ? "" : mPoiItem.getTitle());
        mFabLocation.setOnClickListener(v -> {
            Log.e(TAG,"点击");
            if (0 != mLatitude && 0 != mLongitude){
                LatLng latLng = new LatLng(mLatitude,mLongitude);
                if (null == mLocationMarker){
                    MarkerOptions markerOptions =  new MarkerOptions()
                            .position(new LatLng(mLatitude,mLongitude))
                            .title(mMyLocationAdress).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(),R.mipmap.my_location)));
                    mLocationMarker = mAMap.addMarker(markerOptions);
                }else {
                    mLocationMarker.setPosition(latLng);
                }
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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
        deactivate();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMapView) {
            mMapView.onDestroy();
        }
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
    }


    @Override//定位回调监听器
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.e(TAG,"定位");
        if (null != mLocationChangedListener && null != aMapLocation){
            if (null != aMapLocation && aMapLocation.getErrorCode() == 0){
                mMyLocationAdress = aMapLocation.getAddress();
                mLatitude = aMapLocation.getLatitude();
                mLongitude = aMapLocation.getLongitude();
                Log.e(TAG,"定位成功："+mLatitude+"-"+mLongitude);
//                mLocationChangedListener.onLocationChanged(aMapLocation);//显示系统的小圆点
            }else {
                Toast.makeText(MyApplication.applicationContext,"定位失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override//设置定位初始化以及启动定位,激活定位
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.e(TAG,"激活定位");
        mLocationChangedListener = onLocationChangedListener;
        if (null == mLocationClient){
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);//设置定位监听
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置高精度定位模式
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    @Override//停止定位的相关回调
    public void deactivate() {
        mLocationChangedListener = null;
        if (null != mLocationClient){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}
