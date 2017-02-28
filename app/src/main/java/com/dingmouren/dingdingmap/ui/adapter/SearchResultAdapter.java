package com.dingmouren.dingdingmap.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/28.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<PoiItem> mList;
    public void setList(List<PoiItem> list){
        this.mList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.applicationContext).inflate(R.layout.item_search_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
        }
        private void bindData(PoiItem poiItem){
            if (null != poiItem){
                tvName.setText(poiItem.getTitle());
                tvAddress.setText(poiItem.getSnippet());
            }
        }
    }
}
