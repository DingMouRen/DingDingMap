package com.dingmouren.dingdingmap.ui.welfare;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeImageTransform;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.dingmouren.dingdingmap.Constant;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.bean.GankResultWelfare;
import com.dingmouren.dingdingmap.ui.adapter.WelfareAdapter;
import com.dingmouren.dingdingmap.util.RevealAnimatorUtil;
import com.dingmouren.dingdingmap.util.SPUtil;
import com.dingmouren.dingdingmap.widgets.SpacesItemDecoration;

import java.io.ByteArrayOutputStream;
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
    private SpacesItemDecoration mSpacesItemDecoration;//Item间隔
    private int animatorX ,animatorY;//动画开始和结束的坐标
    private RevealAnimatorUtil revealAnimatorUtil;//揭露动画工具类
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
        animatorX = (int) SPUtil.get(MyApplication.applicationContext, Constant.REVEAL_CENTER_X,this.getWindowManager().getDefaultDisplay().getWidth());//默认值是屏幕宽度
        animatorY = (int) SPUtil.get(MyApplication.applicationContext,Constant.REVEAL_CENTER_Y,this.getWindowManager().getDefaultDisplay().getHeight());//默认值是屏幕高度
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


        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);

        //揭露动画
        revealAnimatorUtil = new RevealAnimatorUtil(mRootLayout,this);
        mRootLayout.post(()-> revealAnimatorUtil.startRevealAnimator(false,animatorX,animatorY));
        getWindow().setSharedElementExitTransition(new ChangeImageTransform());
        getWindow().setSharedElementReenterTransition(new ChangeImageTransform());
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        try {
            mAdapter.setItemOnClickListener((view, imgUrl, position) -> {
                PictureActivity.newInstance(WelfatreActivity.this,view,imgUrl);
            });
        } catch (Exception e) {
            Log.e("error",e.getMessage());
            e.printStackTrace();
        }
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

    @Override
    public void onBackPressed() {
        revealAnimatorUtil.startRevealAnimator(true,animatorX,animatorY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mRootLayout){
            mRootLayout.removeAllViews();
        }
    }
}
