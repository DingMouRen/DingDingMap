package com.dingmouren.dingdingmap.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.dingmouren.dingdingmap.Constant;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.offlinemap.OfflineMapActivity;
import com.dingmouren.dingdingmap.ui.route_plan.RoutePlanActivity;
import com.dingmouren.dingdingmap.ui.search.SearchActivity;
import com.dingmouren.dingdingmap.ui.welfare.WelfatreActivity;
import com.dingmouren.dingdingmap.util.BmbBuilderManager;
import com.dingmouren.dingdingmap.util.SPUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nightonke.boommenu.Animation.OrderEnum;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends BaseActivity implements LocationSource, AMapLocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.search_bar)
    MaterialSearchBar mSearchBar;
    @BindView(R.id.map_mode)
    FabSpeedDial mMapMode;
    @BindView(R.id.fab_location)
    FloatingActionButton mFabLocation;
    @BindView(R.id.fab_check)
    FloatingActionButton mFabCheck;
    @BindView(R.id.bmb)
    BoomMenuButton mBmb;
    @BindView(R.id.fab) FloatingActionButton mFab;
    private AMap mAMap;//地图控制类
    private AMapLocationClient mLocationClient;//AMapLocationClient类对象
    private AMapLocationClientOption mLocationOption;//参数配置对象
    private OnLocationChangedListener mLocationChangedListener;//定位回调监听
    private UiSettings mUiSettings;//操作控件类
    private double mLatitude;//纬度
    private double mLongitude;//经度
    private Marker mLocationMarker;
    boolean isLocated = false;//首次进来定位用的
    private String mCurrentCityName;//定位当前城市名称
    private int bmbSubClickedIndex = -1;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;//判断是否需要检测，防止不停的弹框

    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        for (int i = 0; i < mBmb.getPiecePlaceEnum().pieceNumber(); i++) {
            mBmb.addBuilder(BmbBuilderManager.getSimpleCircleButtonBuilder());
        }

        mMapView.onCreate(savedInstanceState);//创建地图
        if (null == mAMap) {
            mAMap = mMapView.getMap();
        }
        if (null == mUiSettings && null != mAMap) {
            mUiSettings = mAMap.getUiSettings();//获取操作控件类
            mUiSettings.setScaleControlsEnabled(false);//是否显示比例尺控件
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            mUiSettings.setLogoLeftMargin(getWindowManager().getDefaultDisplay().getWidth());//隐藏高德地图的Logo
        }
        //显示地图
        mAMap.setLocationSource(this);//设置定位监听
        mAMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式
        mAMap.showMapText(true);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        setupLocationIcon();//自定义系统的定位图标
        initTrafficEnable();

    }

    @Override
    public void initListener() {
        //获取控件宽高，onCreate中是拿不到控件宽高的
        ViewTreeObserver viewTreeObserver = mBmb.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBmb.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //存储Bmb的中心坐标
                SPUtil.put(MyApplication.applicationContext,Constant.REVEAL_CENTER_X,(int)(mBmb.getX() + mBmb.getWidth()/2));
                SPUtil.put(MyApplication.applicationContext,Constant.REVEAL_CENTER_Y,(int)(mBmb.getY() + mBmb.getHeight()/2));
            }
        });
        mMapMode.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.map_standard:
                        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.map_satellite:
                        mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.map_night:
                        mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
                        break;
                    case R.id.map_navigation:
                        mAMap.setMapType(AMap.MAP_TYPE_NAVI);
                        break;

                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
        mFabLocation.setOnClickListener(v -> {
            if (0 != mLatitude && 0 != mLongitude) {
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                if (null == mLocationMarker) {
                    mLocationMarker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.transparent)));
                } else {
                    mLocationMarker.setPosition(latLng);
                }
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        mFabCheck.setOnClickListener(v -> {
            if (null == mAMap) return;
            if ((boolean) SPUtil.get(MyApplication.applicationContext, Constant.TRAFFIC_ENABLE, true)) {
                mFabCheck.setImageResource(R.mipmap.no_check);
                SPUtil.put(MyApplication.applicationContext, Constant.TRAFFIC_ENABLE, false);
                mAMap.setTrafficEnabled(false);//显示实时路况图层，aMap是地图控制器对象
                Toast.makeText(MyApplication.applicationContext, "关闭实时路况", Toast.LENGTH_SHORT).show();
            } else {
                mFabCheck.setImageResource(R.mipmap.checking);
                SPUtil.put(MyApplication.applicationContext, Constant.TRAFFIC_ENABLE, true);
                mAMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象
                Toast.makeText(MyApplication.applicationContext, "开启实时路况", Toast.LENGTH_SHORT).show();
            }
        });

        mSearchBar.setOnClickListener(v -> {
            SearchActivity.newInstance(MainActivity.this, mCurrentCityName);
        });

        mBmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                Log.e(TAG,"onClicked--index:"+index+"--boomButton:"+boomButton);
                bmbSubClickedIndex = index;
            }

            @Override
            public void onBackgroundClick() {
                Log.e(TAG,"onBackgroundClick");
            }

            @Override
            public void onBoomWillHide() {
                Log.e(TAG,"onBoomWillHide");
            }

            @Override
            public void onBoomDidHide() {
                Log.e(TAG,"onBoomDidHide");
                switch (bmbSubClickedIndex){
                    case 0:
                        break;

                    case 1://路线搜索
                        RoutePlanActivity.newInstance(MainActivity.this,null,null,null,mCurrentCityName,"main");
                        bmbSubClickedIndex = -1;
                        break;

                    case 2:
                        break;

                    case 3://美女图片
                        startActivity(new Intent(MainActivity.this, WelfatreActivity.class));
                        bmbSubClickedIndex = -1;
                        break;

                    case 4://离线地图
                        startActivity(new Intent(MainActivity.this, OfflineMapActivity.class));
                        bmbSubClickedIndex = -1;
                        break;

                    case 5:
                        break;



                }
            }

            @Override
            public void onBoomWillShow() {
                Log.e(TAG,"onBoomWillShow");
            }

            @Override
            public void onBoomDidShow() {
                Log.e(TAG,"onBoomDidShow");
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
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMapView) {
            mMapView.onDestroy();
        }
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override//定位回调监听器
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null == mLocationChangedListener && null == aMapLocation) return;
        if (0 == aMapLocation.getErrorCode()) {//定位成功，成功获取到aMapLocation的信息
            mLatitude = aMapLocation.getLatitude();
            mLongitude = aMapLocation.getLongitude();
            mCurrentCityName = aMapLocation.getCity();
//            parseAMapLocation(aMapLocation);
            mLocationChangedListener.onLocationChanged(aMapLocation);//显示系统的小蓝点
            if (!isLocated) {
                mAMap.moveCamera(CameraUpdateFactory.zoomBy(3));
                isLocated = true;
            }
        } else {//定位失败，
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e(TAG, errText);
        }
    }


    @Override//设置定位初始化以及启动定位
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationChangedListener = onLocationChangedListener;
        if (null == mLocationClient) {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);
            initLocationOptions();//初始化定位参数
            mLocationClient.startLocation();
        }
    }

    @Override//停止定位的相关回调
    public void deactivate() {
        mLocationChangedListener = null;
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }

    }

    /**
     * 定位成功，解析AMapLocation对象
     *
     * @param aMapLocation
     */
    private void parseAMapLocation(AMapLocation aMapLocation) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(aMapLocation.getTime());
        df.format(date);

        Log.e(TAG, "当前定位结果来源：" + aMapLocation.getLocationType()
                + "\n纬度：" + aMapLocation.getLatitude()
                + "\n经度：" + aMapLocation.getLongitude()
                + "\n精度信息：" + aMapLocation.getAccuracy()
                + "\n地址：" + aMapLocation.getAddress()
                + "\n国家信息：" + aMapLocation.getCountry()
                + "\n省信息：" + aMapLocation.getProvince()
                + "\n城市信息：" + aMapLocation.getCity()
                + "\n城区信息：" + aMapLocation.getDistrict()
                + "\n街道信息：" + aMapLocation.getStreet()
                + "\n街道门牌号信息：" + aMapLocation.getStreetNum()
                + "\n城市编码：" + aMapLocation.getCityCode()
                + "\n地区编码：" + aMapLocation.getAdCode()
                + "\n当前定位点的AOI信息：" + aMapLocation.getAoiName()
                + "\n当前室内定位的建筑物Id：" + aMapLocation.getBuildingId()
                + "\n当前室内定位的楼层：" + aMapLocation.getFloor()
                + "\nGPS的当前状态：" + aMapLocation.getGpsAccuracyStatus()
                + "\n定位时间：" + df.toString()
        );
    }

    /**
     * 初始化定位参数
     */
    private void initLocationOptions() {
        if (null != mLocationOption) {
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//同时使用网络定位和GPS定位,优先返回定位结果、地址描述信息
            mLocationOption.setOnceLocationLatest(true);//获取最近三秒内精度最高的一次定位结果
            mLocationOption.setInterval(2000);//默认是连续定位模式，
            mLocationOption.setNeedAddress(true);//设置是否返回地址信息，默认返回地址信息
            mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认是true
            mLocationOption.setHttpTimeOut(30000);//设置定位请求超时时间，默认是30秒
            mLocationOption.setLocationCacheEnable(true);//设置是否使用定位缓存
        }
    }

    /**
     * 检测权限
     *
     * @param permissions
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 自定义的我的位置的图标，自定义系统的小蓝点
     */
    private void setupLocationIcon() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.my_location));//自定义图标
        myLocationStyle.strokeColor(getResources().getColor(R.color.main_color));//自定义精度范围的圆形边框颜色
        myLocationStyle.strokeWidth(5f);//自定义圆形边框的宽度
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));//设置圆形的填充颜色
        mAMap.setMyLocationStyle(myLocationStyle);//将自定义的样式显示在地图上
        Log.e(TAG, "设置自定义样式");
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化路况信息
     */
    private void initTrafficEnable() {
        if (null == mAMap) return;
        if ((boolean) SPUtil.get(MyApplication.applicationContext, Constant.TRAFFIC_ENABLE, true)) {
            mFabCheck.setImageResource(R.mipmap.checking);
            mAMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象
        } else {
            mFabCheck.setImageResource(R.mipmap.no_check);
            mAMap.setTrafficEnabled(false);//显示实时路况图层，aMap是地图控制器对象
        }
    }
}
