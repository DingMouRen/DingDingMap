package com.dingmouren.dingdingmap.bean;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;

public class OfflineChild implements OnClickListener, OnLongClickListener {
	private Context mContext;

	private TextView mOffLineCityName;// 离线包名称

	private TextView mOffLineCitySize;// 离线包大小

	private ImageView mDownloadImage;// 下载相关Image

	private TextView mDownloadProgress;

	private OfflineMapManager amapManager;

	private OfflineMapCity mMapCity;// 离线下载城市


	private boolean mIsDownloading = false;

	private boolean isProvince = false;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int completeCode = (Integer) msg.obj;
			switch (msg.what) {
			case OfflineMapStatus.LOADING:
				
				
				displyaLoadingStatus(completeCode);
				
				
				break;
			case OfflineMapStatus.PAUSE:
				displayPauseStatus(completeCode);
				break;
			case OfflineMapStatus.STOP:
				break;
			case OfflineMapStatus.SUCCESS:
				displaySuccessStatus();
				break;
			case OfflineMapStatus.UNZIP:
				displayUnZIPStatus(completeCode);
				break;
			case OfflineMapStatus.ERROR:
				displayExceptionStatus();
				break;
			case OfflineMapStatus.WAITING:
				displayWaitingStatus(completeCode);
				break;
			case OfflineMapStatus.CHECKUPDATES:
				displayDefault();
				break;
				
			case OfflineMapStatus.EXCEPTION_AMAP:
			case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
			case OfflineMapStatus.EXCEPTION_SDCARD:
				displayExceptionStatus();
				break;
				
			case OfflineMapStatus.NEW_VERSION:
				displayHasNewVersion();
				break;

			}
		}

	};

	public boolean isProvince() {
		return isProvince;
	}

	public void setProvince(boolean isProvince) {
		this.isProvince = isProvince;
	}

	public OfflineChild(Context context, OfflineMapManager offlineMapManager) {
		mContext = context;
		initView();
		amapManager = offlineMapManager;
		// mOfflineMapManager = new OfflineMapManager(mContext, this);
	}

	public String getCityName() {
		if (mMapCity != null) {
			return mMapCity.getCity();
		}
		return null;
	}

	public View getOffLineChildView() {
		return mOffLineChildView;
	}

	private View mOffLineChildView;

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mOffLineChildView = inflater.inflate(R.layout.offlinemap_child, null);
		mOffLineCityName = (TextView) mOffLineChildView.findViewById(R.id.name);
		mOffLineCitySize = (TextView) mOffLineChildView
				.findViewById(R.id.name_size);
		mDownloadImage = (ImageView) mOffLineChildView
				.findViewById(R.id.download_status_image);
		mDownloadProgress = (TextView) mOffLineChildView
				.findViewById(R.id.download_progress_status);

		mOffLineChildView.setOnClickListener(this);
		mOffLineChildView.setOnLongClickListener(this);

	}

	public void setOffLineCity(OfflineMapCity mapCity) {
		if (mapCity != null) {
			mMapCity = mapCity;
			mOffLineCityName.setText(mapCity.getCity());
			double size = ((int) (mapCity.getSize() / 1024.0 / 1024.0 * 100)) / 100.0;
			mOffLineCitySize.setText(String.valueOf(size) + " M");

			notifyViewDisplay(mMapCity.getState(), mMapCity.getcompleteCode(),
					mIsDownloading);
		}
	}

	/**
	 * 更新显示状态 在被点击和下载进度发生改变时会被调用
	 * 
	 * @param status
	 * @param completeCode
	 * @param isDownloading
	 */
	private void notifyViewDisplay(int status, int completeCode,
			boolean isDownloading) {
		if (mMapCity != null) {
			mMapCity.setState(status);
			mMapCity.setCompleteCode(completeCode);
		}
		Message msg = new Message();
		msg.what = status;
		msg.obj = completeCode;
		handler.sendMessage(msg);

	}

	/**
	 * 最原始的状态，未下载，显示下载按钮
	 */
	private void displayDefault() {
		mDownloadProgress.setVisibility(View.INVISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_download);
	}
	
	/**
	 * 显示有更新
	 */
	private void displayHasNewVersion() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_download);
		mDownloadProgress.setText("已下载-有更新");
	}

	/**
	 * 等待中
	 * 
	 * @param completeCode
	 */
	private void displayWaitingStatus(int completeCode) {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
		mDownloadProgress.setTextColor(Color.parseColor("#05C1F1"));
		mDownloadProgress.setText("等待中");
	}
	
	/**
	 * 下载出现异常
	 */
	private void displayExceptionStatus() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
		mDownloadProgress.setTextColor(Color.parseColor("#05C1F1"));
		mDownloadProgress.setText("下载出现异常");
	}

	/**
	 * 暂停
	 * 
	 * @param completeCode
	 */
	private void displayPauseStatus(int completeCode) {
		if (mMapCity != null) {
			completeCode = mMapCity.getcompleteCode();
		}

		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
		mDownloadProgress.setTextColor(Color.parseColor("#05C1F1"));
		mDownloadProgress.setText("暂停中:" + completeCode + "%");

	}

	/**
	 * 下载成功
	 */
	private void displaySuccessStatus() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.GONE);
		mDownloadProgress.setText("安装成功");

		mDownloadProgress.setTextColor(mContext.getResources().getColor(
				android.R.color.darker_gray));
	}

	/**
	 * 正在解压
	 */
	private void displayUnZIPStatus(int completeCode) {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.GONE);
		mDownloadProgress.setText("正在解压: " + completeCode + "%");
		mDownloadProgress.setTextColor(mContext.getResources().getColor(
				android.R.color.darker_gray));
	}

	/**
	 * 
	 * @param completeCode
	 */
	private void displyaLoadingStatus(int completeCode) {
		// todo
		if (mMapCity == null) {
			return;
		}

		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadProgress.setText(mMapCity.getcompleteCode() + "%");
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_stop);
		mDownloadProgress.setTextColor(Color.parseColor("#05C1F1"));
	}

	private synchronized void pauseDownload() {
		amapManager.pause();
//		amapManager.pauseByName(getCityName());
		//暂停下载之后，开始下一个等待中的任务
		amapManager.restart();
	}

	/**
	 * 启动下载任务
	 */
	private synchronized boolean startDownload() {
		try {
			if (isProvince) {
				amapManager.downloadByProvinceName(mMapCity.getCity());
			} else {
				amapManager.downloadByCityName(mMapCity.getCity());
			}
			return true;
		} catch (AMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			Toast.makeText(mContext, e.getErrorMessage(), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public void onClick(View view) {
		


		int completeCode = -1, status = -1;
		if (mMapCity != null) {
			status = mMapCity.getState();
			completeCode = mMapCity.getcompleteCode();

			switch (status) {
			case OfflineMapStatus.UNZIP:
			case OfflineMapStatus.SUCCESS:
				// 解压中何在成功啥不干
				break;
			case OfflineMapStatus.LOADING:
//			case OfflineMapStatus.WAITING:
				pauseDownload();
				// 在下载中的时候点击，表示要暂停
				displayPauseStatus(completeCode);
				break;
			case OfflineMapStatus.PAUSE:
			case OfflineMapStatus.CHECKUPDATES:
			case OfflineMapStatus.ERROR:
			case OfflineMapStatus.WAITING:
//			case OfflineMapStatus.NEW_VERSION:
			default:
				if(startDownload())
					displayWaitingStatus(completeCode);
				else 
					displayExceptionStatus();
				break;
			}
			
			Log.e("zxy-child", mMapCity.getCity() + " " + mMapCity.getState());

		}

	}

	/**
	 * 长按弹出提示框 删除（取消）下载
	 * 加入synchronized 避免在dialog还没有关闭的时候再次，请求弹出的bug
	 */
	public synchronized void showDeleteDialog(final String name) {
		new MaterialDialog.Builder(mContext)
				.title(name)
				.items(R.array.offline_map_del_or_update)
				.itemsCallbackSingleChoice(1,(dialog1, itemView, which, text) -> {
					switch (which){
						case 0:
							amapManager.remove(name);
							break;
						case 1:
							try {
								amapManager.updateOfflineCityByName(name);
							} catch (AMapException e) {
								e.printStackTrace();
							}
							break;
					}
					return true;
				})
				.positiveText("确定")
				.negativeText("取消")
				.onPositive((dialog1, which) -> {
				})
				.onNegative((dialog1, which) -> dialog1.dismiss())
				.show();
	}

	/**
	 * 长按弹出提示框 删除和更新
	 */
	public void showDeleteUpdateDialog(final String name) {
			new MaterialDialog.Builder(mContext)
                    .title(name)
                    .items(R.array.offline_map_del_or_update)
                    .itemsCallbackSingleChoice(1,(dialog1, itemView, which, text) -> {
						switch (which){
							case 0:
								amapManager.remove(name);
								break;
							case 1:
								try {
									amapManager.updateOfflineCityByName(name);
								} catch (AMapException e) {
									e.printStackTrace();
								}
								break;
						}
						return true;
					})
					.positiveText("确定")
					.negativeText("取消")
					.onPositive((dialog1, which) -> {
					})
					.onNegative((dialog1, which) -> dialog1.dismiss())
					.show();
	}

	public boolean onLongClick(View arg0) {
		
		Log.d("amap-longclick",
				mMapCity.getCity() + " : " + mMapCity.getState());
		if (mMapCity.getState() == OfflineMapStatus.SUCCESS) {
			showDeleteUpdateDialog(mMapCity.getCity());
		} else if (mMapCity.getState() != OfflineMapStatus.CHECKUPDATES) {
			showDeleteDialog(mMapCity.getCity());
		} 
		return false;
	}

}
