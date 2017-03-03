package com.dingmouren.dingdingmap.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.DriveSegmentListAdapter;
import com.dingmouren.dingdingmap.util.AMapUtil;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/3.
 */

public class DriveRouteDetailActivity extends BaseActivity {
    private static final String TAG = DriveRouteDetailActivity.class.getName();
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.linear_drive_info)  LinearLayout mLinearInfo;
    @BindView(R.id.firstline)  TextView mFirstLine;
    @BindView(R.id.secondline) TextView mSecondLine;
    @BindView(R.id.listview)  ListView mListView;

    private DrivePath mDrivePath;
    private DriveRouteResult mDriveRouteResult;
    private DriveSegmentListAdapter mDriveSegmentListAdapter;
    public static void newInstance(Activity activity, DrivePath drivePath, DriveRouteResult driveRouteResult){
        Intent intent = new Intent(activity,DriveRouteDetailActivity.class);
        intent.putExtra("drive_path",drivePath);
        intent.putExtra("drive_result",driveRouteResult);
        activity.startActivity(intent);
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (null != getIntent()){
            mDrivePath = getIntent().getParcelableExtra("drive_path");
            mDriveRouteResult = getIntent().getParcelableExtra("drive_result");
        }

    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_drive_route_detail;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mToolbar.setTitle("驾车路线详情");
        setSupportActionBar(mToolbar);
        String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mDrivePath
                .getDistance());
        mFirstLine.setText(dur + "(" + dis + ")");
        int taxiCost = (int) mDriveRouteResult.getTaxiCost();
        if (0 != taxiCost) {
            mSecondLine.setText("打车约" + taxiCost + "元");
            mSecondLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void initData() {
        mDriveSegmentListAdapter = new DriveSegmentListAdapter(MyApplication.applicationContext,mDrivePath.getSteps());
        mListView.setAdapter(mDriveSegmentListAdapter);
    }
}
