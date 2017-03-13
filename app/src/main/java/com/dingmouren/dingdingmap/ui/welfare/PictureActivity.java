package com.dingmouren.dingdingmap.ui.welfare;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import static android.R.attr.path;

/**
 * Created by dingzi on 2016/12/5.
 */

public class PictureActivity extends BaseActivity {
    private static final String TAG = PictureActivity.class.getName();

    private static final String SAVED_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/钉钉地图";
    @BindView(R.id.img_picture)  ImageView img;
    @BindView(R.id.fab_dialog)
    FabSpeedDial mFabDialog;
    @BindView(R.id.tv_no_network) TextView mTvNetNotice;

    private String mImgUrl;

    public static Intent newInstance(Activity context, View view,String imgUrl ){
        Intent intent = new Intent(context,PictureActivity.class);
        intent.putExtra("img_url",imgUrl);
        context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, view, context.getResources().getString(R.string.welfare_share_img)).toBundle());
        return intent;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_picture;
    }

    @Override
    public void init(Bundle savedInstanceStae) {
        if (getIntent() != null){
            mImgUrl = getIntent().getStringExtra("img_url");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        Glide.with(this).load(mImgUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                }).into(img);
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
                        onBackPressed();
                        break;
                    case R.id.img_save:
                        saveImageToGallery(PictureActivity.this,img.getDrawingCache());
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

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DingDingMap");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(MyApplication.applicationContext,"图片保存在"+appDir.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + appDir.getAbsolutePath())));
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
