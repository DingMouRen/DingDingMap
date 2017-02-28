package com.dingmouren.dingdingmap.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/28.
 */

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private List<Tip> mList;

    public TipsAdapter() {
    }

    public void setList(List<Tip> list){
        this.mList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.applicationContext).inflate(R.layout.item_tip,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
        }
        public void bindData(Tip tip){
            if (null != tip){
                tvName.setText(tip.getName());
                tvAddress.setText(tip.getAddress());
            }
        }

    }
}
