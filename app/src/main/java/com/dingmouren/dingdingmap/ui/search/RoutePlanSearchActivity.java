package com.dingmouren.dingdingmap.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.event.EventPoint;
import com.dingmouren.dingdingmap.ui.adapter.SearchResultAdapter;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/4.
 */

public class RoutePlanSearchActivity extends BaseActivity  implements PoiSearch.OnPoiSearchListener {
    private static final String TAG = RoutePlanSearchActivity.class.getName();
    @BindView(R.id.search_bar)  MaterialSearchBar mSearchBar;
    @BindView(R.id.recycler)  RecyclerView mRecycler;
    @BindView(R.id.progressbar)  ProgressBar mProgressBar;
    private SearchResultAdapter mSearchResultAdapter;
    private PoiSearch mPoiSearch;//POI搜索
    private PoiSearch.Query mPoitQuery;//POI查询条件类
    private InputMethodManager inputMethodManager;
    private String mTag ;//起点 终点的标记

    public static void newInstance(Activity activity,String tag){
        Intent intent = new Intent(activity,RoutePlanSearchActivity.class);
        intent.putExtra("tag",tag);
        activity.startActivity(intent);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void init(Bundle savedInstanceStae) {

        if (null != getIntent()){
            mTag = getIntent().getStringExtra("tag");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
        if (null == inputMethodManager)inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == mSearchResultAdapter) mSearchResultAdapter = new SearchResultAdapter();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mSearchResultAdapter);
    }

    @Override
    public void initListener() {
        mSearchBar.enableSearch();
        mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {

            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {
                mPoitQuery = new PoiSearch.Query(String.valueOf(charSequence),"","");
                mPoitQuery.setPageSize(Integer.MAX_VALUE);
                mPoitQuery.setPageNum(1);
                mPoiSearch = new PoiSearch(RoutePlanSearchActivity.this,mPoitQuery);
                mPoiSearch.setOnPoiSearchListener(RoutePlanSearchActivity.this);
                mPoiSearch.searchPOIAsyn();
                mProgressBar.setVisibility(View.VISIBLE);
                if (null != inputMethodManager){//隐藏软件盘
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }

            }

            @Override
            public void onButtonClicked(int i) {

            }
        });

        mSearchResultAdapter.setOnItemClickListener((view, poiItem, position) -> {
            if (mTag.equals("start")){
                Log.e(TAG,"start:"+poiItem.getLatLonPoint().getLatitude() +"-"+poiItem.getLatLonPoint().getLongitude()+"---"+poiItem.getCityCode());
                String title = poiItem.getTitle();
                LatLonPoint startPoint = new LatLonPoint(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
                EventPoint eventPoint = new EventPoint(title,startPoint,0,poiItem.getCityCode());
                EventBus.getDefault().postSticky(eventPoint);
                finish();
            }else if (mTag.equals("end")){
                Log.e(TAG,"end:"+poiItem.getLatLonPoint().getLatitude() +"-"+poiItem.getLatLonPoint().getLongitude()+"---"+poiItem.getCityCode());
                String title = poiItem.getTitle();
                LatLonPoint endPoint = new LatLonPoint(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
                EventPoint eventEndPoint = new EventPoint(title,endPoint,1,poiItem.getCityCode());
                EventBus.getDefault().postSticky(eventEndPoint);
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS /*&& null != poiResult && null != poiResult.getQuery() && poiResult.getQuery() == mPoitQuery*/){
            if (poiResult.getPois().size() != 0) {
                List<PoiItem> poiItems = poiResult.getPois();
                mSearchResultAdapter.setList(poiItems);
                mSearchResultAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }else {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MyApplication.applicationContext,"对不起,没有搜索到相关数据",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MyApplication.applicationContext,"对不起,没有搜索到相关数据",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void getStartOrEnd(EventPoint eventPoint){
//        if (eventPoint.getTag() == 0){
//            Log.e(TAG,"start:"+eventPoint.getTitle()+"-"+eventPoint.getLatLonPoint().toString());
//        }else if(eventPoint.getTag() == 1){
//            Log.e(TAG,"end:"+eventPoint.getTitle()+"-"+eventPoint.getLatLonPoint().toString());
//        }
    }
}
