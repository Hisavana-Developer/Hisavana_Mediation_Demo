package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.IS_DEBUG;
import static com.hisavana.ssp.util.DemoConstants.LOG_TAG;
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
 * DemoSplashActivity
 * 启动后展示的 Splash 的 Activity, 广告 SDK 在此类初始化。
 *
 * Created  ON 2023/7/3
 *
 * @author :fangxuhui
 */
public class DemoSplashActivity extends AppCompatActivity implements PrivacyAgreementDialog.OnClickListener {

    private String SLOT_ID = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_SPLASH : SLOT_ID_SPLASH;
    private TSplashAd tSplashAd;
    private Handler delayHandler = new Handler(Looper.getMainLooper());
    private boolean isShowAd;

    private TSplashView tSplashView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_splash);
        tSplashView = findViewById(R.id.splash_ad);

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
            Log.d(LOG_TAG, "展示弹窗");
            PrivacyAgreementDialog dialog = new PrivacyAgreementDialog(this);
            dialog.show(getSupportFragmentManager(), "privacy");
        }
    }

    // TODO Init AdManger
    private void initAd() {
        // 广告SDK 设置 -->
        AdxServerConfig.setAppModle(AdxServerConfig.TEST);
        TAdManager.init(this, new TAdManager.AdConfigBuilder()
                .setAppId(DemoConstants.IS_DEBUG ? DemoConstants.TEST_APP_ID : DemoConstants.APP_ID) // 必须设置，请在广告平台申请
                .setDebug(IS_DEBUG) // 可选项，是否打印广告日志，默认为false；假如设置为true时会打印log，关键字ADSDK_M、ADSDK_N
                .testDevice(!IS_DEBUG) // 可选项，是否请求测试广告，默认为false；假如为true时请求广告平台的测试广告，否则请求广告平台的正式广告
                .setMuteVideo(false) // 可选项，视频类广告是否全局静音
                .isInitAdMob(false)  // 可选项，是否在此时进行admob的初始化操作
                .setInternalDefaultAdVersion(1710853164130L) // 可选项，假如开启内置广告功能需要设置，值为zip包中的版本号，用于兜底广告的更新 （如何配置Zip包 兜底广告请联系运营）
                .setCloudCompleteListener(new TAdManager.OnCloudCompleteListener() {
                    @Override
                    public void onCloudComplete(int code, String message) {

                    }
                }) // 可选项，该回调配置完成，最长等待15s
                .build());
    }

    private void loadAd() {
        if (tSplashAd == null) {
            // 绑定开屏广告对象、广告位ID
            tSplashAd = new TSplashAd(this, SLOT_ID);
            // 构建并设置广告请求体
            tSplashAd.setRequestBody( new TAdRequestBody.AdRequestBodyBuild()
                    .setAdListener(new AdListener(this)).build());
            // 设置跳过动作
            tSplashAd.setOnSkipListener(new SkipListener(this));
        }
        // 加载广告后在设置的等待时间内将最优广告回调返回
        tSplashAd.loadAd();
    }

    private void goToMain(){
        delayHandler.removeCallbacks(mRunnable);
        Intent intent = new Intent(this,DemoMainActivity.class);
        startActivity(intent);
        finishPage();
    }

    private void showAd(){
        // 可选项 也可以这种形式创建 TSplashView tSplashView = new TSplashView(this);
        tSplashAd.showAd(tSplashView);
        /**
         * 可选项
         * 对于需要统计到达广告场景的应用，可自行设置场景值
         * 主要目的是统计当前广告场景利用率，第一个参数自定义场景名称，第二个参数广告数量
         */
        // String sceneToken = tSplashAd.enterScene("splash_scene_name", 1);
        // tSplashAd.showAd(tSplashView, sceneToken);

        /**
         * 可选项
         * 允许应用通过添加子布局的方式传入logo和名称
         * sdk内部加载子布局渲染底部媒体logo信息，底部区域为手机屏幕高度的20%
         */
         // View logoLayout = LayoutInflater.from(this).inflate(R.layout.splash_logo_layout, null, false);
         // tSplashAd.showAd(tSplashView, logoLayout);

        /**
         * 可选项
         * 同时设置到达广告场景和添加子布局
         */
        // String sceneToken = tSplashAd.enterScene("splash_scene_id", 1);
        // View logoLayout = LayoutInflater.from(this).inflate(R.layout.splash_logo_layout, null, false);
        // tSplashAd.showAd(tSplashView, logoLayout, sceneToken);
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
        // 注意及时释放 destroy 否则容易发生内存泄漏
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

    // 广告监听器，监听广告的请求、填充、展示、点击、异常、关闭动作的回调
    public static class AdListener extends TAdListener {

        private boolean isClosed;

        private final WeakReference<DemoSplashActivity>  activityWeakReference;

        public AdListener(DemoSplashActivity demoSplashActivity){
            activityWeakReference = new WeakReference<>(demoSplashActivity);
        }

        @Override
        public void onLoad() {
            // Request success
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null){
                activity.isShowAd = true;
                activity.showAd();
            }
        }

        @Override
        public void onError(TAdErrorCode tAdErrorCode) {
            // Request failed
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null) {
                activity.goToMain();
            }
        }

        @Override
        public void onShow(int source) {
            // Source 请参考 接入文档
            // Called when an ad is displayed
        }

        @Override
        public void onClicked(int source) {
            // Called when an ad is clicked
        }

        @Override
        public void onClosed(int source) {
            // Called when an ad close
            DemoSplashActivity activity = activityWeakReference.get();
            if(activity != null && !isClosed) {
                activity.goToMain();
                isClosed = true;
            }
            //选择合适时机 注意及时释放tSplashAd destroy 否则容易发生内存泄漏
        }

        @Override
        public void onShowError(@Nullable TAdErrorCode tAdErrorCode) {
            super.onShowError(tAdErrorCode);
            // Called when an ad show failed
        }
    }
    // splash 跳过动作监听器
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
