package com.dingmouren.dingdingmap.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.dingmouren.dingdingmap.bean.OfflineChild;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 维护已经下载和下载过程中的列表
 */
public class OfflineDownloadedAdapter extends BaseAdapter {

	private OfflineMapManager mOfflineMapManager;

	private List<OfflineMapCity> cities = new ArrayList<OfflineMapCity>();

	private Context mContext;

	private OfflineChild currentOfflineChild;
	
	public OfflineDownloadedAdapter(Context context,
									OfflineMapManager offlineMapManager) {
		this.mContext = context;
		this.mOfflineMapManager = offlineMapManager;
		initCityList();

	}

	/**
	 * 重新初始化数据加载数据
	 */
	public void notifyDataChange() {
		long start = System.currentTimeMillis();
		initCityList();
		Log.d("amap", "Offline Downloading notifyData cost: " + (System.currentTimeMillis() -start));
	}

	private void initCityList() {
		if (cities != null) {
			long start = System.currentTimeMillis();
			for (Iterator it = cities.iterator(); it.hasNext();) {
				OfflineMapCity i = (OfflineMapCity) it.next();
				it.remove();
			}
			Log.d("amap", "Offline Downloading notifyData cities iterator cost: " + (System.currentTimeMillis() -start));
			// arraylist中的元素不能这样移除
//			for (OfflineMapCity mapCity : cities) {
//				cities.remove(mapCity);
//				mapCity = null;
//			}
		}

		long start = System.currentTimeMillis();
		cities.addAll(mOfflineMapManager.getDownloadOfflineMapCityList());
		cities.addAll(mOfflineMapManager.getDownloadingCityList());
		Log.d("amap", "Offline Downloading notifyData getDownloadingCityList cost: " + (System.currentTimeMillis() -start));
		
		start = System.currentTimeMillis();
		notifyDataSetChanged();
		Log.d("amap", "Offline Downloading notifyData notifyDataSetChanged cost: " + (System.currentTimeMillis() -start));
		
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public Object getItem(int index) {
		return cities.get(index);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public int getItemViewType(int position) {

		return 0;
	}

	public final class ViewHolder {
		public OfflineChild mOfflineChild;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			viewHolder = new ViewHolder();

			currentOfflineChild = new OfflineChild(mContext,
					mOfflineMapManager);
			convertView = currentOfflineChild.getOffLineChildView();
			viewHolder.mOfflineChild = currentOfflineChild;
			convertView.setTag(viewHolder);
		}
		OfflineMapCity offlineMapCity = (OfflineMapCity) getItem(position);
		viewHolder.mOfflineChild.setOffLineCity(offlineMapCity);

		return convertView;

	}
}
