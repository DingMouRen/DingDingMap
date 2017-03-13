package com.dingmouren.dingdingmap.ui.welfare;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.bean.GankResultWelfare;
import com.dingmouren.dingdingmap.ui.adapter.WelfareAdapter;
import com.dingmouren.dingdingmap.widgets.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * Created by mouren on 2017/3/12.
 */

public class WelfatreActivity extends BaseActivity implements WelfareContract.View {
    @BindView(R.id.root_layout)  CoordinatorLayout mRootLayout;
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.swipe_refresh)  SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.recycler)  RecyclerView mRecycler;

    private WelfarePresenter mPresenter;
    public StaggeredGridLayoutManager mLayoutManager;
    private WelfareAdapter mAdapter;
    private SpacesItemDecoration mSpacesItemDecoration;
    @Override
    public int setLayoutId() {
        return R.layout.activity_welfare;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        mAdapter = new WelfareAdapter();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mToolbar.setNavigationIcon(R.mipmap.back_arrow);
        mToolbar.setTitle("美女福利多多");
        setSupportActionBar(mToolbar);
        if (mSwipeRefresh != null){
            mSwipeRefresh.setColorSchemeResources(R.color.main_color);//设置进度动画的颜色
//            mSwipeRefresh.setProgressBackgroundColorSchemeResource(android.R.color.holo_blue_bright);//设置进度圈背景颜色
            //这里进行单位换算  第一个参数是单位，第二个参数是单位数值，这里最终返回的是24dp对相应的px值
            mSwipeRefresh.setProgressViewOffset(true,0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,24,getResources().getDisplayMetrics()));
            mSwipeRefresh.setOnRefreshListener(()->{
                if (mAdapter != null && null != mPresenter){
                    mAdapter.clearList();
                    mPresenter.initPage();
                    mPresenter.requestData();
                }
            });
        }


        mLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        mSpacesItemDecoration = new SpacesItemDecoration(15);
        mRecycler.addItemDecoration(mSpacesItemDecoration);
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> finish());
        mAdapter.setItemOnClickListener((view, imgUrl, position) -> {
            PictureActivity.newInstance(WelfatreActivity.this,imgUrl );
        });
    }

    @Override
    public void initData() {
        mPresenter = new WelfarePresenter((WelfareContract.View) this);
        mPresenter.addScrollerListener();
        mPresenter.requestData();
    }

    @Override
    public void setDataRefresh(boolean refresh) {
        if (refresh){
            mSwipeRefresh.setRefreshing(true);
        }else {
            new Handler().postDelayed(()-> mSwipeRefresh.setRefreshing(false),800);
        }
    }

    @Override
    public StaggeredGridLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    @Override
    public void setData(List<GankResultWelfare> list) {
        mAdapter.addList(list);
        mAdapter.notifyDataSetChanged();
        setDataRefresh(false);
    }
}
