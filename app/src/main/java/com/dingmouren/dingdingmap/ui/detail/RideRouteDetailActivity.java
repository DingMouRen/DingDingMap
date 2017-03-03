package com.dingmouren.dingdingmap.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.RidePath;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.RideSegmentListAdapter;
import com.dingmouren.dingdingmap.util.AMapUtil;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/3.
 */

public class RideRouteDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.linear_info)  LinearLayout mLinerInfo;
    @BindView(R.id.firstline)  TextView mFirstLine;
    @BindView(R.id.secondline) TextView mSecondLine;
    @BindView(R.id.listview) ListView mListView;
    private RidePath mRidePath;
    private RideSegmentListAdapter mRideSegmentListAdapter;

    public static void newInstance(Activity activity, RidePath ridePath){
        Intent intent = new Intent(activity,RideRouteDetailActivity.class);
        intent.putExtra("ride_path",ridePath);
        activity.startActivity(intent);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_ride_route_detail;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (null != getIntent()){
            mRidePath = getIntent().getParcelableExtra("ride_path");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mToolbar.setTitle("骑行路线详情");
        setSupportActionBar(mToolbar);
        String dur = AMapUtil.getFriendlyTime((int) mRidePath.getDuration());
        String dis = AMapUtil .getFriendlyLength((int) mRidePath.getDistance());
        mFirstLine.setText(dur + "(" + dis + ")");
        mRideSegmentListAdapter = new RideSegmentListAdapter(
                this.getApplicationContext(), mRidePath.getSteps());
        mListView.setAdapter(mRideSegmentListAdapter);
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void initData() {

    }
}
