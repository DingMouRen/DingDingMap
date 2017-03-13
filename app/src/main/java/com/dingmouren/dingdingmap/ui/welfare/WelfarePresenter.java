package com.dingmouren.dingdingmap.ui.welfare;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.api.ApiManager;
import com.dingmouren.dingdingmap.bean.GankResultWelfare;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mouren on 2017/3/12.
 */

public class WelfarePresenter  implements WelfareContract.Presenter<WelfareContract.View> {

    public WelfareContract.View mView;
    public StaggeredGridLayoutManager mLayoutManager;
    public RecyclerView mRecycler;
    private int mPage = 1;
    private boolean isSlidingToLast = false;
    public WelfarePresenter(WelfareContract.View view){
        this.mView = view;
        mLayoutManager = mView.getLayoutManager();
        mRecycler = mView.getRecyclerView();
    }

    @Override
    public void requestData() {
        mView.setDataRefresh(true);
        ApiManager.getApiInstance().getApiService().getGirlPics(mPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listGankResult -> displayData(listGankResult.results),this::loadError);
    }


    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        mPage--;
        mView.setDataRefresh(false);
        Toast.makeText(MyApplication.applicationContext,"请检查网络设置",Toast.LENGTH_SHORT).show();
    }

    public void initPage(){
        mPage = 1;
    }


    public void displayData(List<GankResultWelfare> list) {
        mView.setData(list);
    }

    /**
     * 滑动到底部的监听
     */
    public void addScrollerListener(){
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               if (newState == RecyclerView.SCROLL_STATE_IDLE){
                   //获取最后一个完全显示的ItemPosition
                   int[] lastVisiblePositions = mLayoutManager.findLastVisibleItemPositions(new int[mLayoutManager.getSpanCount()]);
                   int lastVisiblePosition = getMaxElem(lastVisiblePositions);
                   int totalItemCount = mLayoutManager.getItemCount();
                   //判断是否滑动到底部
                   if (lastVisiblePosition == (totalItemCount -1) && isSlidingToLast){
                       mPage++;
                       requestData();
                   }
               }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.e("page","onScrolled:" + dy);
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向 dy用来判断纵向滑动方向
                if (dy > 0){
                    isSlidingToLast = true;//标记向下滑动
                }else {
                    isSlidingToLast = false;
                }

            }
        });
    }

    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i]>maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }
}
