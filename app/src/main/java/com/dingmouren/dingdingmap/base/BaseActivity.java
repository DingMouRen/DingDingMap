package com.dingmouren.dingdingmap.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.ButterKnife;

/**
 * Created by dingmouren on 2017/2/25.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        init(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        initView(savedInstanceState);
        initListener();
        initData();
    }
    public  void init(Bundle savedInstanceStae){}
    public abstract int setLayoutId();
    public abstract void initView(Bundle savedInstanceState);
    public void initListener(){}
    public abstract void initData();
}
