package com.dingmouren.dingdingmap.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;

/**
 * Created by dingmouren on 2017/3/8.
 */

public class CustomRecyclerView extends RecyclerView {
    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof LinearLayoutManager) {
            super.setLayoutManager(layout);
        }else {
            throw new ClassCastException("请使用LinearLayoutManager");
        }
    }
    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        if (getAdapter() != null && getLayoutManager() instanceof LinearLayoutManager){
            LayoutAnimationController.AnimationParameters animationParameters = ( LayoutAnimationController.AnimationParameters)params.layoutAnimationParameters;
            if (animationParameters == null){
                AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                alphaAnimation.setDuration(1000);
                animationParameters = new LayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParameters;
            }
            animationParameters.count = count;
            animationParameters.index = index;
        }else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }
}
