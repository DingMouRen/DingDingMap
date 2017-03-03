package com.dingmouren.dingdingmap.ui.search_result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.route_plan.RoutePlanActivity;
import com.dingmouren.dingdingmap.ui.search.SearchActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;

import butterknife.BindView;

/**
 * Created by mouren on 2017/2/28.
 */

public class SearchResultActivity extends BaseActivity implements LocationSource,AMapLocationListener {
    private static final String TAG = SearchResultActivity.class.getName();
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
    private Marker mLocationMarker;
    private String mMyLocationAdress;
    private LatLng myLatLng;
    private LatLng destnationLatLng;
    private LatLonPoint mStartPoint;//起点
    private LatLonPoint mEndPoint ;//终点
    private DecimalFormat decimalFormat;//保留小数点用的
    private String mCurrentCity ;

    public static void newInstance(Activity activity, PoiItem poiItem ){
        Intent intent = new Intent(activity,SearchResultActivity.class);
        intent.putExtra("poiItem",poiItem);
        activity.startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_route_detail;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (null != getIntent()){
        mPoiItem = getIntent().getParcelableExtra("poiItem");
            mCurrentCity = mPoiItem.getCityName();
            Log.e(TAG,"mCurrentCity:" + mCurrentCity);
        }
        destnationLatLng = new LatLng(mPoiItem.getLatLonPoint().getLatitude(),mPoiItem.getLatLonPoint().getLongitude());
        mEndPoint = new LatLonPoint(mPoiItem.getLatLonPoint().getLatitude(),mPoiItem.getLatLonPoint().getLongitude());

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
                .position(destnationLatLng)
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
        mSearchBar.setOnClickListener(v -> startActivity(new Intent(SearchResultActivity.this,SearchActivity.class)));
        mFabLocation.setOnClickListener(v -> {
            if (null != myLatLng){
                if (null == mLocationMarker && null != myLatLng){
                    MarkerOptions markerOptions =  new MarkerOptions()
                            .position(myLatLng)
                            .title(mMyLocationAdress).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(),R.mipmap.my_location)));
                    mLocationMarker = mAMap.addMarker(markerOptions);
                }else {
                    mLocationMarker.setPosition(myLatLng);
                }
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));
            }
        });

        mFabToWhere.setOnClickListener(v -> {
            Log.e(TAG,"mCurrentCity:" +mCurrentCity);
            RoutePlanActivity.newInstance(this,mStartPoint,mEndPoint,mCurrentCity,mPoiItem.getTitle());
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
        if (null != mLocationChangedListener && null != aMapLocation){
            if (null != aMapLocation && aMapLocation.getErrorCode() == 0){
                mMyLocationAdress = aMapLocation.getAddress();
                myLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                mStartPoint = new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                if (null != myLatLng && null != destnationLatLng){
                    decimalFormat = new DecimalFormat(".0");
                    mDistance.setText(decimalFormat.format(AMapUtils.calculateLineDistance(myLatLng,destnationLatLng)/1000) +"公里");
                }
//                mLocationChangedListener.onLocationChanged(aMapLocation);//显示系统的小圆点

            }else {
                Toast.makeText(MyApplication.applicationContext,"定位失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override//设置定位初始化以及启动定位,激活定位
    public void activate(OnLocationChangedListener onLocationChangedListener) {
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
