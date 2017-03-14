package com.dingmouren.dingdingmap.ui.offlinemap;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.dingmouren.dingdingmap.Constant;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.ui.adapter.OfflineDownloadedAdapter;
import com.dingmouren.dingdingmap.ui.adapter.OfflineListAdapter;
import com.dingmouren.dingdingmap.ui.adapter.OfflinePagerAdapter;
import com.dingmouren.dingdingmap.util.RevealAnimatorUtil;
import com.dingmouren.dingdingmap.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/3/8.
 */

public class OfflineMapActivity extends BaseActivity implements OfflineMapManager.OfflineMapDownloadListener
        , View.OnClickListener, OfflineMapManager.OfflineLoadedListener {
    @BindView(R.id.img_back)
    ImageView mImageBack;
    @BindView(R.id.download_list_text)
    TextView mDownloadText;
    @BindView(R.id.downloaded_list_text)
    TextView mDownloadedText;
    @BindView(R.id.content_viewpage)
    ViewPager mContentViewPage;
    @BindView(R.id.container)
    RelativeLayout mRootLayout;

    private ExpandableListView mAllOfflineMapList;
    private ListView mDownLoadedList;
    private OfflineListAdapter adapter;
    private OfflineDownloadedAdapter mDownloadedAdapter;
    private PagerAdapter mPageAdapter;
    private ProgressDialog initDialog;// 刚进入该页面时初始化弹出的dialog
    private OfflineMapManager amapManager;//离线地图下载控制器
    private final static int UPDATE_LIST = 0;//更新所有列表
    private final static int SHOW_MSG = 1;//显示toast log
    private final static int DISMISS_INIT_DIALOG = 2;
    private final static int SHOW_INIT_DIALOG = 3;
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
    private int animatorX ,animatorY;//动画开始和结束的坐标
    private RevealAnimatorUtil revealAnimatorUtil;//揭露动画工具类

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    if (mContentViewPage.getCurrentItem() == 0) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                    } else {
                        mDownloadedAdapter.notifyDataChange();
                    }
                    break;
                case SHOW_MSG:
                    Toast.makeText(MyApplication.applicationContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case DISMISS_INIT_DIALOG:
                    initDialog.dismiss();
                    handler.sendEmptyMessage(UPDATE_LIST);
                    break;
                case SHOW_INIT_DIALOG:
                    if (initDialog != null) {
                        initDialog.show();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public int setLayoutId() {
        return R.layout.activity_offlinemap;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        animatorX = (int) SPUtil.get(MyApplication.applicationContext, Constant.REVEAL_CENTER_X,this.getWindowManager().getDefaultDisplay().getWidth());//默认值是屏幕宽度
        animatorY = (int) SPUtil.get(MyApplication.applicationContext,Constant.REVEAL_CENTER_Y,this.getWindowManager().getDefaultDisplay().getHeight());//默认值是屏幕高度
        amapManager = new OfflineMapManager(this, this);//构造离线地图类
        amapManager.setOnOfflineLoadedListener(this);
        initDialog();
        //揭露动画
        revealAnimatorUtil = new RevealAnimatorUtil(mRootLayout,this);
//        mRootLayout.post(()-> revealAnimatorUtil.startRevealAnimator(false,animatorX,animatorY));进入的时候不使用揭露动画
    }

    @Override
    public void initListener() {
        mImageBack.setOnClickListener(this);
        mDownloadText.setOnClickListener(this);
        mDownloadedText.setOnClickListener(this);
        mContentViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int paddingHorizontal = mDownloadedText.getPaddingLeft();
                int paddingVertical = mDownloadedText.getPaddingTop();
                switch (position) {
                    case 0:
                        mDownloadText.setBackground(getResources().getDrawable(R.drawable.offline_left_checked));
                        mDownloadText.setTextColor(Color.WHITE);
                        mDownloadedText.setBackground(getResources().getDrawable(R.drawable.offline_right_normal));
                        mDownloadText.setTextColor(Color.GRAY);
                        break;
                    case 1:
                        mDownloadText.setBackground(getResources().getDrawable(R.drawable.offline_left_normal));
                        mDownloadText.setTextColor(Color.GRAY);
                        mDownloadedText.setBackground(getResources().getDrawable(R.drawable.offline_right_checked));
                        mDownloadedText.setTextColor(Color.WHITE);
                        break;
                }
                handler.sendEmptyMessage(UPDATE_LIST);
                mDownloadedText.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
                mDownloadText.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (amapManager != null) {
            amapManager.destroy();
        }

        if (initDialog != null) {
            initDialog.dismiss();
            initDialog.cancel();
        }
    }


    @Override
    public void onBackPressed() {
        revealAnimatorUtil.startRevealAnimator(true,animatorX,animatorY);
    }

    @Override//OfflineLoadedListener
    public void onVerifyComplete() {
        initAllCityList();
        initDownloadedList();
        initViewpage();
        dissmissDialog();
    }

    @Override//OfflineMapManager
    public void onDownload(int status, int completeCode, String downName) {
        switch (status) {
            case OfflineMapStatus.SUCCESS:
                break;
            case OfflineMapStatus.LOADING:
                Log.d("amap-download", "download: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.UNZIP:
                Log.d("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.WAITING:
                Log.d("amap-waiting", "WAITING: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.PAUSE:
                Log.d("amap-pause", "pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                Log.e("amap-download", "download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
                Log.e("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                Log.e("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING " + downName);
                Toast.makeText(OfflineMapActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                Log.e("amap-download", "download: " + " EXCEPTION_SDCARD " + downName);
                break;
            default:
                break;
        }

        handler.sendEmptyMessage(UPDATE_LIST);
    }

    @Override//OfflineMapManager
    public void onCheckUpdate(boolean hasNew, String name) {
        Log.i("amap-demo", "onCheckUpdate " + name + " : " + hasNew);
        Message message = new Message();
        message.what = SHOW_MSG;
        message.obj = name + "地图数据" + (hasNew == true ? "有更新":"已经是最新");
        handler.sendMessage(message);
    }

    @Override//OfflineMapManager
    public void onRemove(boolean success, String name, String describe) {
        Log.i("amap-demo", "删除 " + name + " 离线地图" + (success == true ? "成功":"失败"));
        handler.sendEmptyMessage(UPDATE_LIST);
        Message message = new Message();
        message.what = SHOW_MSG;
        message.obj = "删除" + name + "离线地图" + (success == true ? "成功":"失败");
        handler.sendMessage(message);
    }

    @Override//OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_list_text:
                int paddingHorizontal = mDownloadText.getPaddingLeft();
                int paddingVertical = mDownloadText.getPaddingTop();
                mContentViewPage.setCurrentItem(0);

                mDownloadText.setBackground(MyApplication.applicationContext.getResources().getDrawable(R.drawable.offline_left_checked));
                mDownloadText.setTextColor(Color.WHITE);
                mDownloadedText.setBackground(MyApplication.applicationContext.getResources().getDrawable(R.drawable.offline_right_normal));
                mDownloadedText.setTextColor(Color.GRAY);
                mDownloadedText.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

                mDownloadText.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

                mDownloadedAdapter.notifyDataChange();
                break;
            case R.id.downloaded_list_text:
                int paddingHorizontal1 = mDownloadedText.getPaddingLeft();
                int paddingVertical1 = mDownloadedText.getPaddingTop();
                mContentViewPage.setCurrentItem(1);

                mDownloadText.setBackground (MyApplication.applicationContext.getResources().getDrawable(R.drawable.offline_left_normal));
                mDownloadText.setTextColor(Color.GRAY);
                mDownloadedText.setBackground (MyApplication.applicationContext.getResources().getDrawable(R.drawable.offline_right_checked));
                mDownloadedText.setTextColor(Color.WHITE);
                mDownloadedText.setPadding(paddingHorizontal1, paddingVertical1, paddingHorizontal1, paddingVertical1);
                mDownloadText.setPadding(paddingHorizontal1, paddingVertical1, paddingHorizontal1, paddingVertical1);

                mDownloadedAdapter.notifyDataChange();
                break;
            case R.id.img_back:
               onBackPressed();
                break;
        }
    }


    /**
     * 初始化如果已下载的城市多的话，会比较耗时
     */
    private void initDialog() {
        if (initDialog == null)
            initDialog = new ProgressDialog(this);
        initDialog.setMessage("正在获取离线城市列表");
        initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initDialog.setIndeterminate(false);
        initDialog.setCancelable(false);
        initDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissDialog() {
        if (initDialog != null) {
            initDialog.dismiss();
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initViewpage() {
        mPageAdapter = new OfflinePagerAdapter(mContentViewPage,
                mAllOfflineMapList, mDownLoadedList);

        mContentViewPage.setAdapter(mPageAdapter);
        mContentViewPage.setCurrentItem(0);

    }

    /**
     * 初始化所有城市列表
     */
    public void initAllCityList() {
        // 扩展列表
        View provinceContainer = LayoutInflater.from(OfflineMapActivity.this)
                .inflate(R.layout.offline_province_listview, null);
        mAllOfflineMapList = (ExpandableListView) provinceContainer
                .findViewById(R.id.province_download_list);
        initProvinceListAndCityMap();
        adapter = new OfflineListAdapter(provinceList, amapManager,
                OfflineMapActivity.this);
        // 为列表绑定数据源
        mAllOfflineMapList.setAdapter(adapter);
        // adapter实现了扩展列表的展开与合并监听
        mAllOfflineMapList.setOnGroupCollapseListener(adapter);
        mAllOfflineMapList.setOnGroupExpandListener(adapter);
        mAllOfflineMapList.setGroupIndicator(null);
    }

    private void initProvinceListAndCityMap() {

        List<OfflineMapProvince> lists = amapManager
                .getOfflineMapProvinceList();

        provinceList.add(null);
        provinceList.add(null);
        provinceList.add(null);
        // 添加3个null 以防后面添加出现 index out of bounds

        ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

        for (int i = 0; i < lists.size(); i++) {
            OfflineMapProvince province = lists.get(i);
            if (province.getCityList().size() != 1) {
                // 普通省份
                provinceList.add(i + 3, province);
                // cityMap.put(i + 3, cities);
            } else {
                String name = province.getProvinceName();
                if (name.contains("香港")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("澳门")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("全国概要图")) {
                    gaiyaotuList.addAll(province.getCityList());
                } else {
                    // 直辖市
                    cityList.addAll(province.getCityList());
                }
            }
        }
        // 添加，概要图，直辖市，港口
        OfflineMapProvince gaiyaotu = new OfflineMapProvince();
        gaiyaotu.setProvinceName("概要图");
        gaiyaotu.setCityList(gaiyaotuList);
        provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

        OfflineMapProvince zhixiashi = new OfflineMapProvince();
        zhixiashi.setProvinceName("直辖市");
        zhixiashi.setCityList(cityList);
        provinceList.set(1, zhixiashi);

        OfflineMapProvince gaogao = new OfflineMapProvince();
        gaogao.setProvinceName("港澳");
        gaogao.setCityList(gangaoList);
        provinceList.set(2, gaogao);
    }

    /**
     * 初始化已下载列表
     */
    public void initDownloadedList() {
        mDownLoadedList = (ListView) LayoutInflater.from(
                OfflineMapActivity.this).inflate(
                R.layout.offline_downloaded_list, null);
        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
        mDownLoadedList.setLayoutParams(params);
        mDownloadedAdapter = new OfflineDownloadedAdapter(this, amapManager);
        mDownLoadedList.setAdapter(mDownloadedAdapter);
    }
}
