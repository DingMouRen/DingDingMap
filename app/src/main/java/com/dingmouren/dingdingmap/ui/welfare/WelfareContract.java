package com.dingmouren.dingdingmap.ui.welfare;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;


import com.dingmouren.dingdingmap.base.BasePresenter;
import com.dingmouren.dingdingmap.base.BaseView;
import com.dingmouren.dingdingmap.bean.GankResultWelfare;

import java.util.List;

/**
 * Created by dingmouren on 2016/12/1.
 */

public interface WelfareContract {

    interface View extends BaseView {
        void setDataRefresh(boolean refresh);
        StaggeredGridLayoutManager getLayoutManager();
        RecyclerView getRecyclerView();
        void setData(List<GankResultWelfare> list);
    }

    interface Presenter<V extends BaseView> extends BasePresenter<View> {
        void requestData();
    }

}
