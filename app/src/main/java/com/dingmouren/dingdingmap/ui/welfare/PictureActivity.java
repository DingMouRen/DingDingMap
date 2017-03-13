package com.dingmouren.dingdingmap.ui.welfare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.internal.NavigationMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmap.MyApplication;
import com.dingmouren.dingdingmap.R;
import com.dingmouren.dingdingmap.base.BaseActivity;
import com.dingmouren.dingdingmap.util.NetworkUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dingzi on 2016/12/5.
 */

public class PictureActivity extends BaseActivity {
    private static final String TAG = PictureActivity.class.getName();
    private static final String BITMAP = "bitmap";
    private static final String IMG_URL = "img_url";

    private static final String SAVED_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/钉钉地图";
    @BindView(R.id.img_picture)  ImageView img;
    @BindView(R.id.fab_dialog)
    FabSpeedDial mFabDialog;
    @BindView(R.id.tv_no_network) TextView mTvNetNotice;

    private Bitmap mBitmap;
    private String mImgUrl;

    public static Intent newInstance(Context  context,String imgUrl ){
        Intent intent = new Intent(context,PictureActivity.class);
        intent.putExtra(IMG_URL,imgUrl);
        context.startActivity(intent);
        return intent;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_picture;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (getIntent() != null){
            mImgUrl = getIntent().getStringExtra(IMG_URL);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        Glide.with(this).load(mImgUrl).asBitmap().centerCrop().into(img);
        if (!NetworkUtil.isAvailable(this)){
            mTvNetNotice.setVisibility(View.VISIBLE);
        }else {
            mTvNetNotice.setVisibility(View.GONE);
        }
        new PhotoViewAttacher(img);
    }

    @Override
    public void initListener() {
        mFabDialog.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.back:
                        finish();
                        break;
                    case R.id.img_save:
                        saveImage();
                        break;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    @Override
    public void initData() {
    }





    /**
     * 保存图片
     */
    private void saveImage() {
        Toast.makeText(MyApplication.applicationContext,"图片保存在"+ SAVED_PATH,Toast.LENGTH_SHORT).show();
        img.buildDrawingCache();
        Bitmap bitmap = img.getDrawingCache();
        //将bitmap转换成二进制，写入本地
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        File dir = new File(SAVED_PATH);
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,mImgUrl + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(byteArray,0,byteArray.length);
            fos.flush();
            //使用广播通知系统相册进行更新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            PictureActivity.this.sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(img);
    }
}
