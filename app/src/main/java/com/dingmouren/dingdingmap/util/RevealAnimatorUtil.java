package com.dingmouren.dingdingmap.util;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.lang.ref.WeakReference;

/**
 * Created by dingmouren on 2017/3/13.
 */

public class RevealAnimatorUtil {
    private WeakReference<View> weakRootLayout;
    private WeakReference<Activity> weakActivity;

    public RevealAnimatorUtil(View view, Activity activity) {
        this.weakRootLayout = new WeakReference<View>(view);
        this.weakActivity = new WeakReference<Activity>(activity);
    }

    public  void startRevealAnimator(boolean reversed, int x, int y){
        View mRootLayout = weakRootLayout.get();
        Activity mActivity = weakActivity.get();
        if (mActivity == null || mRootLayout == null) return;
        float hypot = (float) Math.hypot(mRootLayout.getHeight(),mRootLayout.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout,x,y,startRadius,endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed){
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            View mRootLayout = weakRootLayout.get();
            Activity mActivity = weakActivity.get();
            if (mActivity == null || mRootLayout == null) return;
            mRootLayout.setVisibility(View.INVISIBLE);
            mActivity.finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

}
