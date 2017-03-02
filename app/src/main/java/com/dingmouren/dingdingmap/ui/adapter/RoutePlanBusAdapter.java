package com.dingmouren.dingdingmap.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.listener.ItemBusRouteOnClickListener;
import com.dingmouren.dingdingmap.util.AMapUtil;

import java.util.List;

/**
 * Created by dingmouren on 2017/3/2.
 */

public class RoutePlanBusAdapter extends RecyclerView.Adapter<RoutePlanBusAdapter.ViewHolder> {
    private BusRouteResult mBusRouteResult;
    private List<BusPath> mBusPathList;
    private ItemBusRouteOnClickListener mListener;
    public RoutePlanBusAdapter(BusRouteResult mBusRouteResult) {
        this.mBusRouteResult = mBusRouteResult;
        this.mBusPathList = mBusRouteResult.getPaths();
    }

    public void setOnItemClickListener(ItemBusRouteOnClickListener listener){
        this.mListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.applicationContext).inflate(R.layout.item_route_plan_bus,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mBusPathList.get(position));
        holder.root.setOnClickListener(v -> {
            if (null != mListener){
                mListener.onClick(holder.root,mBusPathList.get(position),mBusRouteResult,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mBusPathList ? 0 : mBusPathList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,des;
        RelativeLayout root;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.bus_path_title);
            des = (TextView) itemView.findViewById(R.id.bus_path_des);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
        }

        private void bindData(BusPath busPath){
            title.setText(AMapUtil.getBusPathTitle(busPath));
            des.setText(AMapUtil.getBusPathDes(busPath));
        }

    }
}
