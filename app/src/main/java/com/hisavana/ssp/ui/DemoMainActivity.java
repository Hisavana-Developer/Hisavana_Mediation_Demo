package com.hisavana.ssp.ui;

import static com.transsion.core.utils.PermissionUtil.lacksPermissions;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cloud.sdk.commonutil.control.AdxPreferencesHelper;
import com.hisavana.mediation.config.TAdManager;
import com.hisavana.ssp.BuildConfig;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;
import com.hisavana.ssp.video.ADMediationVideoActivity;

/**
 * @author peng.sun
 * @data 2017/7/5
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class DemoMainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mNativeAd;
    private RelativeLayout mInterstitialAd;
    private RelativeLayout mBannerAd;
    private RelativeLayout mSplashAd;
    private RelativeLayout mMediaVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 显示当前引入 SDK 版本号
        TextView tvSDKVersion = findViewById(R.id.tvSDKVersion);
        // com.hisavana.common.BuildConfig
        tvSDKVersion.setText("Current SDK Version is "+BuildConfig.sdk_version);

        initView();
        initListener();
        initData();
        // 申请权限
//        getPermissions(this,1,
//                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE});

    }

    private void initView() {
        mNativeAd = findViewById(R.id.nativeBtn);
        mSplashAd = findViewById(R.id.splashBtn);
        mBannerAd = findViewById(R.id.bannerBtn);
        mInterstitialAd = findViewById(R.id.interstitialBtn);
        mMediaVideoAd = findViewById(R.id.media_videoBtn);
    }

    private void initListener() {
        mNativeAd.setOnClickListener(this);
        mSplashAd.setOnClickListener(this);
        mBannerAd.setOnClickListener(this);
        mInterstitialAd.setOnClickListener(this);
        mMediaVideoAd.setOnClickListener(this);
    }

    private void initData() {

    }

    public void getPermissions(Activity activity, int requestCode, String... permissions) {
        /**
         * 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lacksPermissions(permissions)) { //缺少权限
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
        }
    }

//    public void onClickHotApp(View view) {
//        Intent intent = new Intent(this, HotAppActivity.class);
//        startActivity(intent);
//    }

    public void clickAgreement(View view) {
        Uri uri = Uri.parse("http://h5.eagllwin.com/doc/rule-detail.html?id=80");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void clickPrivacy(View view) {
        Uri uri = Uri.parse("https://h5.eagllwin.com/doc/rule-detail.html?id=78");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nativeBtn:
                startActivity(new Intent(this, NativeMainActivity.class));
                break;
            case R.id.splashBtn:
                startActivity(new Intent(this, SplashAdActivity.class));
                break;
            case R.id.interstitialBtn:
                Intent intent = new Intent(this, InterstitialActivity.class);
                startActivity(intent);
                break;
            case R.id.bannerBtn:
                startActivity(new Intent(this, BannerAdActivity.class));
                break;
            case R.id.media_videoBtn:
                startActivity(new Intent(this, ADMediationVideoActivity.class));
                break;
        }
    }
}
