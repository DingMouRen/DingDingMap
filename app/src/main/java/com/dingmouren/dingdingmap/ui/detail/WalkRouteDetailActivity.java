package com.dingmouren.dingdingmap.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.WalkSegmentListAdapter;
import com.dingmouren.dingdingmap.util.AMapUtil;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/3.
 */

public class WalkRouteDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.firstline)  TextView mFirstLine;
    @BindView(R.id.linear_info) LinearLayout mLinearInfo;
    @BindView(R.id.listview) ListView mListView;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;
    private WalkPath mWalkPath;

    public static void newInstance(Activity activity, WalkPath walkPath, WalkRouteResult walkRouteResult){
        Intent intent = new Intent(activity,WalkRouteDetailActivity.class);
        intent.putExtra("walk_path",walkPath);
        intent.putExtra("walk_result",walkRouteResult);
        activity.startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_walk_route_detail;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (null != getIntent()){
            mWalkPath = getIntent().getParcelableExtra("walk_path");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mToolbar.setTitle("步行路线详情");
        setSupportActionBar(mToolbar);
        String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AMapUtil .getFriendlyLength((int) mWalkPath.getDistance());
        mFirstLine.setText(dur + "(" + dis + ")");
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        mListView.setAdapter(mWalkSegmentListAdapter);
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void initData() {

    }
}
