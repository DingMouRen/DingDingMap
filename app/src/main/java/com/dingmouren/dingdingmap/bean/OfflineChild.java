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

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.dingmouren.dingdingmap.R;

public class OfflineChild implements OnClickListener, OnLongClickListener {
	private Context mContext;

	private TextView mOffLineCityName;// 离线包名称

	private TextView mOffLineCitySize;// 离线包大小

	private ImageView mDownloadImage;// 下载相关Image

	private TextView mDownloadProgress;

	private OfflineMapManager amapManager;

	private OfflineMapCity mMapCity;// 离线下载城市

	Dialog dialog;// 长按弹出的对话框

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
		mDownloadProgress.setTextColor(Color.GREEN);
		mDownloadProgress.setText("等待中");
	}
	
	/**
	 * 下载出现异常
	 */
	private void displayExceptionStatus() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
		mDownloadProgress.setTextColor(Color.RED);
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
		mDownloadProgress.setTextColor(Color.RED);
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
		mDownloadProgress.setTextColor(Color.BLUE);
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
		
//		if(mMapCity.getCity() .equals( "北京")) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					for(int i =0; i< 100;i++) {
//						try {
//							amapManager.downloadByCityName("北京");
//							TimeUnit.MILLISECONDS.sleep(500);
//							amapManager.downloadByCityName("上海");
//							TimeUnit.MILLISECONDS.sleep(500);
//						} catch (AMapException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				
//				}
//			}).start();
//			return;
//		}
		
//		// 避免频繁点击事件，避免不断从夫开始下载和暂停下载
//		mOffLineChildView.setEnabled(false);
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mOffLineChildView.setEnabled(true);
//			}
//		},100);// 这个时间段刚刚好

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
//					Toast.makeText(mContext, "SD卡空间不多了", 1000).show();
				// 在暂停中点击，表示要开始下载
				// 在默认状态点击，表示开始下载
				// 在等待中点击，表示要开始下载
				// 要开始下载状态改为等待中，再回调中会自己修改
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
		AlertDialog.Builder builder = new Builder(mContext);

		builder.setTitle(name);
		builder.setSingleChoiceItems(new String[] { "删除" }, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialog.dismiss();
						if (amapManager == null) {
							return;
						}
						switch (arg1) {
						case 0:
							amapManager.remove(name);
							break;

						default:
							break;
						}

						// amapManager.log();

					}
				});
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 长按弹出提示框 删除和更新
	 */
	public void showDeleteUpdateDialog(final String name) {
		AlertDialog.Builder builder = new Builder(mContext);

		builder.setTitle(name);
		builder.setSingleChoiceItems(new String[] { "删除", "检查更新" }, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialog.dismiss();
						if (amapManager == null) {
							return;
						}
						switch (arg1) {
						case 0:
							amapManager.remove(name);
							break;
						case 1:
							try {
								amapManager.updateOfflineCityByName(name);
							} catch (AMapException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						default:
							break;
						}

					}
				});
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	public boolean onLongClick(View arg0) {
		
//		if (mMapCity.getState() == OfflineMapStatus.LOADING) {
//			amapManager.restart();
//			return false;
//		} 
		
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
