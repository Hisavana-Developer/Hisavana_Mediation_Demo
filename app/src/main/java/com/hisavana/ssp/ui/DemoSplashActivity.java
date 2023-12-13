package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.IS_DEBUG;
import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_SPLASH;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_SPLASH;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.cloud.hisavana.sdk.config.AdxServerConfig;
import com.cloud.sdk.commonutil.control.AdxPreferencesHelper;
import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.interfacz.OnSkipListener;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.mediation.ad.TSplashAd;
import com.hisavana.mediation.ad.TSplashView;
import com.hisavana.mediation.config.TAdManager;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;

import java.lang.ref.WeakReference;

/**
 * Created  ON 2023/7/3
 *
 * @author :fangxuhui
 */
public class DemoSplashActivity extends AppCompatActivity implements PrivacyAgreementDialog.OnClickListener {

    private String SLOT_ID = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_SPLASH : SLOT_ID_SPLASH;
    private TSplashAd tSplashAd;
    private Handler delayHandler = new Handler(Looper.getMainLooper());
    private boolean isShowAd;
    private boolean isGoToMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_splash);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setAttributes(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController windowInsetsController = getWindow().getInsetsController();
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                return false;
            }
        });


        boolean aBoolean = AdxPreferencesHelper.getInstance().getBoolean(DemoConstants.USER_AGREE_PRIVACY);
        if (aBoolean) {
            initAd();
            loadAd();
            delayHandler.postDelayed(mRunnable,2000);
        } else {
            Log.d("fangxuhui", "展示弹窗");
            PrivacyAgreementDialog dialog = new PrivacyAgreementDialog(this);
            dialog.show(getSupportFragmentManager(), "privacy");
        }
    }

    private void initAd() {
        // 广告SDK 设置 -->
        AdxServerConfig.setAppModle(AdxServerConfig.TEST);
        TAdManager.init(this, new TAdManager.AdConfigBuilder()
                .setAppId(DemoConstants.IS_DEBUG ? DemoConstants.TEST_APP_ID : DemoConstants.APP_ID)
                .setDebug(IS_DEBUG)
                .testDevice(!IS_DEBUG)
                .build());
    }

    private void loadAd() {
        if (tSplashAd == null) {
            tSplashAd = new TSplashAd(this, SLOT_ID);
            tSplashAd.setRequestBody( new TAdRequestBody.AdRequestBodyBuild()
                    .setAdListener(new AdListener(this)).build());
            tSplashAd.setOnSkipListener(new SkipListener(this));
        }
        tSplashAd.loadAd();
    }

    private void goToMain(){
        delayHandler.removeCallbacks(mRunnable);
        Intent intent = new Intent(this,DemoMainActivity.class);
        startActivity(intent);
        finishPage();
    }

    private void showAd(){
        TSplashView tSplashView = new TSplashView(this);
        tSplashAd.showAd(tSplashView);
    }

    private void finishPage() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    @Override
    public void agree() {
        initAd();
        loadAd();
        delayHandler.postDelayed(mRunnable,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tSplashAd != null){
            tSplashAd.destroy();
        }
        delayHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isShowAd){
                goToMain();
            }
        }
    };

    public  static class AdListener extends TAdListener {

        private boolean isClosed;

        private final WeakReference<DemoSplashActivity>  activityWeakReference;

        public AdListener(DemoSplashActivity demoSplashActivity){
            activityWeakReference = new WeakReference<>(demoSplashActivity);
        }

        @Override
        public void onError(TAdErrorCode tAdErrorCode) {
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null) {
                activity.goToMain();
            }
        }

        @Override
        public void onShow(int i) {

        }

        @Override
        public void onClicked(int i) {

        }

        @Override
        public void onClosed(int i) {
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null && !isClosed) {
                activity.goToMain();
                isClosed = true;
            }
        }

        @Override
        public void onLoad() {
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null){
                activity.isShowAd = true;
                activity.showAd();
            }
        }
    }

    public  static class SkipListener implements OnSkipListener {
        private WeakReference<DemoSplashActivity>  activityWeakReference;

        public SkipListener(DemoSplashActivity demoSplashActivity){
            activityWeakReference = new WeakReference<>(demoSplashActivity);
        }
        @Override
        public void onClick() {
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null) {
                activity.goToMain();
            }
        }

        @Override
        public void onTimeReach() {
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null) {
                activity.goToMain();
            }
        }
    }
}
