package com.dingmouren.dingdingmap.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.ui.adapter.SearchResultAdapter;
import com.dingmouren.dingdingmap.ui.routedetail.RouteDetailActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;




public class SearchActivity extends FragmentActivity implements PoiSearch.OnPoiSearchListener{
    private static final String TAG = SearchActivity.class.getName();
    @BindView(R.id.search_bar)  MaterialSearchBar mSearchBar;
    @BindView(R.id.recycler)  RecyclerView mRecycler;
    @BindView(R.id.progressbar) ProgressBar mProgressBar;
    private SearchResultAdapter mSearchResultAdapter;
    private PoiSearch mPoiSearch;//POI搜索
    private PoiSearch.Query mPoitQuery;//POI查询条件类
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        initListener();
    }

    private void init() {
        if (null == inputMethodManager)inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == mSearchResultAdapter) mSearchResultAdapter = new SearchResultAdapter();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mSearchResultAdapter);
    }

    private void initListener() {
         mSearchBar.enableSearch();
        mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {

            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {
                Log.e(TAG,"搜索："+charSequence);
                mPoitQuery = new PoiSearch.Query(String.valueOf(charSequence),"","聊城");
                mPoitQuery.setPageSize(10);
                mPoitQuery.setPageNum(1);
                mPoiSearch = new PoiSearch(SearchActivity.this,mPoitQuery);
                mPoiSearch.setOnPoiSearchListener(SearchActivity.this);
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
        mSearchResultAdapter.setOnItemClickListener((view, poiItem, position) -> RouteDetailActivity.newInstance(SearchActivity.this,poiItem));
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
//        Log.e(TAG,"rCode："+rCode);
//        Log.e(TAG,"poiResult："+poiResult.toString());
//        List<PoiItem> poiItems = poiResult.getPois();
//        Log.e(TAG,"poiItems："+poiItems.toString());
//        for (int i = 0; i < poiItems.size(); i++) {
//            Log.e(TAG,"地址："+poiItems.get(i).getSnippet());
//        }
//        mSearchResultAdapter.setList(poiItems);
//        mSearchResultAdapter.notifyDataSetChanged();
        if (rCode == AMapException.CODE_AMAP_SUCCESS /*&& null != poiResult && null != poiResult.getQuery() && poiResult.getQuery() == mPoitQuery*/){
            List<PoiItem> poiItems = poiResult.getPois();
            mSearchResultAdapter.setList(poiItems);
            mSearchResultAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.INVISIBLE);
        }else {
            Toast.makeText(MyApplication.applicationContext,"对不起,没有搜索到相关数据",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {

    }
}

