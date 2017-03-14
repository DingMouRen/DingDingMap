package com.dingmouren.dingdingmap.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.home.MainActivity;
import com.dingmouren.dingdingmap.widgets.TimerTextView;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/14.
 */

public class SplashActivtiy extends BaseActivity {
    @BindView(R.id.tv_count_down)TimerTextView mTvCountDown;
    @BindView(R.id.lottie_view)  LottieAnimationView mLottieView;
    @BindView(R.id.fonts)  LinearLayout mFontsLayout;
    @Override
    public int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTvCountDown.setTimes(5);
        mTvCountDown.beginRun();
    }

    @Override
    public void initListener() {
        mTvCountDown.setOnClickListener(v -> {
            if (!mTvCountDown.isRun()){
                startActivity(new Intent(this, MainActivity.class));
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
    }
}
