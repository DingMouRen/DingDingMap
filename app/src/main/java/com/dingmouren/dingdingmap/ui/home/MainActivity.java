package com.dingmouren.dingdingmap.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends BaseActivity implements  LocationSource, AMapLocationListener ,ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.mapview) MapView mMapView;
    @BindView(R.id.search_bar)  FloatingSearchView mSearchBar;
    @BindView(R.id.map_mode)   FabSpeedDial mMapMode;
    private AMap mAMap;//地图控制类
    private AMapLocationClient mLocationClient ;//AMapLocationClient类对象
    private AMapLocationClientOption mLocationOption ;//参数配置对象
    private OnLocationChangedListener mLocationChangedListener;//定位回调监听

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
        mMapView.onCreate(savedInstanceState);//创建地图
        if (null == mAMap){
            mAMap = mMapView.getMap();
        }
        //显示地图
        mAMap.setLocationSource(this);//设置定位监听
        mAMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式
        mAMap.showMapText(true);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);

    }

    @Override
    public void initListener() {
        mMapMode.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
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

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.linear_nearby,R.id.linear_route,R.id.linear_mine})
    public void onClick(View view){
            switch (view.getId()){
                case R.id.linear_nearby:
                    Toast.makeText(MyApplication.applicationContext,"附近",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_route:
                    Toast.makeText(MyApplication.applicationContext,"路线",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_mine:
                    Toast.makeText(MyApplication.applicationContext,"我的",Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if(isNeedCheck){
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
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
    }
    @Override//定位回调监听器
    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (null == mLocationChangedListener && null == aMapLocation) return;
        Log.e(TAG,"执行到了");
        if (0 == aMapLocation.getErrorCode()){//定位成功，成功获取到aMapLocation的信息
            Log.e(TAG,"定位成功");
            parseAMapLocation(aMapLocation);
            mLocationChangedListener.onLocationChanged(aMapLocation);//显示系统的小蓝点
        }else {//定位失败，
            String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
            Log.e(TAG,errText);
        }
    }


    @Override//设置定位初始化以及启动定位
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationChangedListener = onLocationChangedListener;
        if (null == mLocationClient){
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
        if (null != mLocationClient){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }

    }

    /**
     * 定位成功，解析AMapLocation对象
     * @param aMapLocation
     */
    private void parseAMapLocation(AMapLocation aMapLocation) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(aMapLocation.getTime());
        df.format(date);

        Log.e(TAG,"当前定位结果来源："+aMapLocation.getLocationType()
        +"\n纬度：" + aMapLocation.getLatitude()
        +"\n经度：" + aMapLocation.getLongitude()
        +"\n精度信息：" + aMapLocation.getAccuracy()
        +"\n地址：" + aMapLocation.getAddress()
        +"\n国家信息："+ aMapLocation.getCountry()
        +"\n省信息：" + aMapLocation.getProvince()
        +"\n城市信息：" + aMapLocation.getCity()
        +"\n城区信息：" + aMapLocation.getDistrict()
        +"\n街道信息：" + aMapLocation.getStreet()
        +"\n街道门牌号信息：" + aMapLocation.getStreetNum()
        +"\n城市编码：" + aMapLocation.getCityCode()
        +"\n地区编码：" + aMapLocation.getAdCode()
        +"\n当前定位点的AOI信息：" + aMapLocation.getAoiName()
        +"\n当前室内定位的建筑物Id：" + aMapLocation.getBuildingId()
        +"\n当前室内定位的楼层：" + aMapLocation.getFloor()
        +"\nGPS的当前状态：" + aMapLocation.getGpsAccuracyStatus()
        +"\n定位时间：" +  df.toString()
        );
    }

    /**
     * 初始化定位参数
     */
    private void initLocationOptions() {
        if (null != mLocationOption){
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
     *
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
     * @param grantResults
     * @return
     * @since 2.5.0
     *
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
     *
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
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
