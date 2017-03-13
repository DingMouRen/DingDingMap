package com.dingmouren.dingdingmap.ui.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dingmouren.dingdingmap.Constant;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.util.RevealAnimatorUtil;
import com.dingmouren.dingdingmap.util.SPUtil;

import butterknife.BindView;

/**
 * Created by mouren on 2017/3/13.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.root_layout) CoordinatorLayout mRootLayout;
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.collapsing)  CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.nestedScrollView) NestedScrollView mNestedScrollView;
    @BindView(R.id.tv_version_name)  TextView mTvVersionName;
    private int animatorX ,animatorY;//动画开始和结束的坐标
    private RevealAnimatorUtil revealAnimatorUtil;//揭露动画工具类
    @Override
    public int setLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        animatorX = (int) SPUtil.get(MyApplication.applicationContext, Constant.REVEAL_CENTER_X,this.getWindowManager().getDefaultDisplay().getWidth());//默认值是屏幕宽度
        animatorY = (int) SPUtil.get(MyApplication.applicationContext,Constant.REVEAL_CENTER_Y,this.getWindowManager().getDefaultDisplay().getHeight());//默认值是屏幕高度
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.back_arrow);
        setSupportActionBar(mToolbar);
        mCollapsing.setTitle("");
        mCollapsing.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsing.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        mTvVersionName.setText("v"+getAppVersionName());
        //揭露动画
        revealAnimatorUtil = new RevealAnimatorUtil(mRootLayout,this);
        mRootLayout.post(()-> revealAnimatorUtil.startRevealAnimator(false,animatorX,animatorY));
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void initData() {

    }

    @Override
    public void onBackPressed() {
        revealAnimatorUtil.startRevealAnimator(true,animatorX,animatorY);
    }


    /**
     * 返回当前程序版本名
     */
    public  String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }

}
