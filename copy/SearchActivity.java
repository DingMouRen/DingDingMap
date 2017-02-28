package com.dingmouren.dingdingmap.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.util.PoiOverlay;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dingmouren on 2017/2/27.
 */

public class SearchActivity extends FragmentActivity implements PoiSearch.OnPoiSearchListener ,
        Inputtips.InputtipsListener,BusStationSearch.OnBusStationSearchListener,
        BusLineSearch.OnBusLineSearchListener,WeatherSearch.OnWeatherSearchListener,
        RouteSearch.OnRouteSearchListener{
    private static final String TAG = SearchActivity.class.getName();
    @BindView(R.id.search_bar)  MaterialSearchBar mSearchBar;
    private AMap mAMap;//地图控制类
    private PoiSearch mPoiSearch;//POI搜索
    private PoiSearch.Query mQuery;//POI查询条件类
    private PoiResult mPoiResult;//POI返回的结果
    private BusStationQuery mBusStationQuery;//公交站点查询
    private BusStationSearch mBusStationSearch;//公交站点查询对象
    private BusLineQuery mBusLineQuery;//公交线路查询
    private BusLineSearch mBusLineSearch;//公交路线查询对象
    private WeatherSearchQuery mWeatherQuery;//天气查询
    private WeatherSearch mWeatcherSearch;//天气查询对象
    private RouteSearch mRouteSearch;//驾车出行路线规划
    private RouteSearch.DriveRouteQuery mDiveRouteQuery;//驾车出行查找

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initMap();
        initListener();
    }


    private void initMap() {
        if (null == mAMap){
            mAMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        //poi查询
        mQuery = new PoiSearch.Query("光岳楼","","聊城");
        mQuery.setPageSize(10);
        mQuery.setPageNum(1);
        mPoiSearch = new PoiSearch(this,mQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        mPoiSearch.searchPOIAsyn();
        //公交站点查询
        mBusStationQuery = new BusStationQuery("姜韩","聊城");
        mBusStationSearch = new BusStationSearch(this,mBusStationQuery);
        mBusStationSearch.setOnBusStationSearchListener(this);
        mBusStationSearch.searchBusStationAsyn();
        //公交路线查询
        mBusLineQuery = new BusLineQuery("351", BusLineQuery.SearchType.BY_LINE_NAME,"聊城");
        mBusLineQuery.setPageSize(10);
        mBusLineQuery.setPageNumber(0);
        mBusLineSearch = new BusLineSearch(this,mBusLineQuery);
        mBusLineSearch.setOnBusLineSearchListener(this);
        mBusLineSearch.searchBusLineAsyn();
        //天气查询
        mWeatherQuery = new WeatherSearchQuery("聊城",WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mWeatcherSearch = new WeatherSearch(this);
        mWeatcherSearch.setOnWeatherSearchListener(this);
        mWeatcherSearch.setQuery(mWeatherQuery);
        mWeatcherSearch.searchWeatherAsyn();

    }


    private void initListener() {
        mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {

            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {

            }

            @Override
            public void onButtonClicked(int i) {

            }
        });
        mSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newStr = s.toString().trim();
                InputtipsQuery inputQuery = new InputtipsQuery(newStr,"聊城");
                inputQuery.setCityLimit(true);
                Inputtips inputTips = new Inputtips(MyApplication.applicationContext,inputQuery);
                inputTips.setInputtipsListener(SearchActivity.this);
                inputTips.requestInputtipsAsyn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override//兴趣点搜索
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        mPoiResult = poiResult;
        List<PoiItem> poiItems = mPoiResult.getPois();
        List<SuggestionCity> suggestingCities = mPoiResult.getSearchSuggestionCitys();
         mAMap.clear();//清理之前的图标
        PoiOverlay poiOverlay = new PoiOverlay(mAMap,poiItems);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
        for (int i = 0; i < poiItems.size(); i++) {
            Log.e(TAG,"行政区域代码："+poiItems.get(i).getAdCode()
                      +"\n行政区域名称：" +poiItems.get(i).getAdName()
                      +"\nPOI所在商圈：" +poiItems.get(i).getBusinessArea()
                      +"\nPOI的城市编码：" +poiItems.get(i).getCityCode()
                      +"\nPOI的城市名称：" +poiItems.get(i).getCityName()
                      +"\n逆地理编码查询时POI坐标点相对于地理坐标点的方向：" +poiItems.get(i).getDirection()
                      +"\n POI 距离中心点的距离：" +poiItems.get(i).getDistance()
                      +"\nPOI的电子邮件地址：" +poiItems.get(i).getEmail()
                      +"\nPOI入口经纬度：" +poiItems.get(i).getEnter()
                      +"\nPOI出口经纬度：" +poiItems.get(i).getExit()
                      +"\nPOI的室内信息，如楼层、建筑物：" +poiItems.get(i).getIndoorData()
                      +"\nPOI的经纬度坐标：" +poiItems.get(i).getLatLonPoint()
                      +"\nPOI的停车场类型：" +poiItems.get(i).getParkingType()
                      +"\nPOI的省/自治区/直辖市/特别行政区名称：" +poiItems.get(i).getProvinceName()
                      +"\nPOI的地址：" +poiItems.get(i).getSnippet()
                      +"\nPOI的电话号码：" +poiItems.get(i).getTel()
                      +"\nPOI的名称：" +poiItems.get(i).getTitle()
                      +"\nPOI 的类型描述：" +poiItems.get(i).getTypeDes()

            );
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {

    }

    @Override//输入提醒
    public void onGetInputtips(List<Tip> list, int rCode) {
        for (int i = 0; i < list.size(); i++) {
            Log.e("tip-" ,"详细地址："+list.get(i).getAddress()
                            +"\n提示区域:"+list.get(i).getDistrict()
                            +"\n提示名称:"+list.get(i).getName()
                            +"\nPoi的经纬度:"+list.get(i).getPoint()
                            +"\n输入提示结果的类型编码:"+list.get(i).getTypeCode()
            );
        }
    }

    @Override//公交站点查询
    public void onBusStationSearched(BusStationResult busStationResult, int rCode) {
        Log.e("gongjiao","执行了公交站点查询");
        Log.e("gongjiao","rCode："+rCode);

        List<BusStationItem> busStationItems = busStationResult.getBusStations();
        for (int j = 0; j < busStationItems.size(); j++) {
            Log.e("gongjiao","车站区域编码："+busStationItems.get(j).getAdCode()
                                +"\n车站Id："+busStationItems.get(j).getBusStationId()
                                +"\n车站名称："+busStationItems.get(j).getBusStationName()
                                +"\n车站城市编码："+busStationItems.get(j).getCityCode()
                                +"\n车站经纬度："+busStationItems.get(j).getLatLonPoint()
            );
            List<BusLineItem> busLineItems =  busStationItems.get(j).getBusLineItems();
            Log.e("gongjiao","busLineItems："+busLineItems.toString());

        }
    }

    @Override//公交路线查询
    public void onBusLineSearched(BusLineResult busLineResult, int rCode) {
        Log.e("gongjiao","执行了公交线路查询");
        Log.e("gongjiao","rCode："+rCode);
        List<BusLineItem> busLineItems = busLineResult.getBusLines();
        Log.e("gongjiao","busLineItems："+busLineItems.toString());
        for (int k = 0; k < busLineItems.size(); k++) {
            Log.e("gongjiao","公交线路的起步价:"+busLineItems.get(k).getBasicPrice()
//                    +"\n公交线路外包矩形的左下与右上顶点坐标："+busLineItems.get(k).getBounds()
//                    +"\n公交线路所属的公交公司："+busLineItems.get(k).getBusCompany()
//                    +"\n公交线路的唯一ID："+busLineItems.get(k).getBusLineId()
//                    +"\n公交线路的名称，包含线路编号和文字名称、类型、首发站、终点站："+busLineItems.get(k).getBusLineName()
//                    +"\n公交线路的类型："+busLineItems.get(k).getBusLineType()
//                    +"\n公交线路的站点列表："+busLineItems.get(k).getBusStations().toString()
//                    +"\n公交线路的城市编码："+busLineItems.get(k).getCityCode()
//                    +"\n公交线路的沿途坐标，包含首发站和终点站坐标："+busLineItems.get(k).getDirectionsCoordinates().toString()
                    +"\n公交线路全程里程："+busLineItems.get(k).getDistance()
                    +"\n公交线路的首班车时间："+busLineItems.get(k).getFirstBusTime()
                    +"\n公交线路的末班车时间："+busLineItems.get(k).getLastBusTime()
                    +"\n公交线路的始发站名称："+busLineItems.get(k).getOriginatingStation()
                    +"\n公交线路的终点站名称："+busLineItems.get(k).getTerminalStation()
                    +"\n公交线路的全程票价："+busLineItems.get(k).getTotalPrice()
            );
        }
    }

    @Override//天气实况查询
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();
        Log.e("weather","行政区划代码："+ weatherLive.getAdCode()
                          +"\n城市名称："+weatherLive.getCity()
                          +"\n空气湿度的百分比："+weatherLive.getHumidity()
                          +"\n省份名称："+weatherLive.getProvince()
                          +"\n实时数据发布时间："+weatherLive.getReportTime()
                          +"\n实时气温："+weatherLive.getTemperature()
                          +"\n天气现象描述："+weatherLive.getWeather()
                          +"\n风向："+weatherLive.getWindDirection()
                          +"\n风力："+weatherLive. 	getWindPower()
        );

    }

    @Override//天气预报查询
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
    //出行路线规划
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
