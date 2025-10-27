package com.mediation.ssp.ui;

import static com.mediation.ssp.util.DemoConstants.SLOT_ID_SPLASH;
import static com.mediation.ssp.util.DemoConstants.TEST_SLOT_ID_SPLASH;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.interfacz.OnSkipListener;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TSplashAd;
import com.hisavana.mediation.ad.TSplashView;
import com.mediation.ssp.R;
import com.mediation.ssp.util.DemoConstants;
import com.transsion.core.utils.ToastUtil;

import java.lang.ref.WeakReference;

/**
 * 开屏广告Demo
 */
public class SplashAdActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 开屏广告 View
     */
    private TSplashView adView;
    private TSplashAd tSplashAd;
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_SPLASH : SLOT_ID_SPLASH;
    private String sceneToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("Splash");
        setContentView(R.layout.splash_ad);
        super.onCreate(savedInstanceState);
        tvADStatus = findViewById(R.id.tvADStatus);
        adView = findViewById(R.id.splash_ad);
        showAdStatus("Ready to load ads");
    }

    @Override
    protected void initListener() {
        super.initListener();
        findViewById(R.id.preload_ad_btn).setOnClickListener(this);
        findViewById(R.id.load_ad_btn).setOnClickListener(this);
        findViewById(R.id.show_ad_btn).setOnClickListener(this);
    }

    /**
     * 检查 开屏广告 是否存在缓存
     */
    public void onClickCheckCache(View view) {
        String hasCache;
        // hasCache 当前是否有可用广告
        if (TSplashAd.hasCache(DemoConstants.SLOT_ID_SPLASH)) {
            hasCache = "开屏广告 缓存中 -有- 广告";
        } else {
            hasCache = "开屏广告 缓存中 -无- 广告";
        }
        ToastUtil.showLongToast(hasCache);
    }

    private void goToHome() {
        AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> go to home");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注意及时释放 destroy 否则容易发生内存泄漏
        if (tSplashAd != null) {
            tSplashAd.destroy();
            tSplashAd = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preload_ad_btn:
                showAdStatus("Preload Ad loading...");
                if (tSplashAd == null) {
                    //广告位ID
                    tSplashAd = new TSplashAd(this, mSlotId);
                    tSplashAd.setRequestBody(
                            new TAdRequestBody.AdRequestBodyBuild()
                                    .setAdListener(new TAdAlliance(this)).build());
                    tSplashAd.setOnSkipListener(new TOnSkipListener());
                }
                if(TextUtils.isEmpty(sceneToken)){
                    /**
                     * 可选项
                     * 对于需要统计到达广告场景的应用，可自行设置场景值
                     * 主要目的是统计当前广告场景利用率，第一个参数自定义场景名称，第二个参数广告数量
                     */
                    sceneToken = tSplashAd.enterScene("splash_scene_id",1);
                }
                tSplashAd.preload();
                break;
            case R.id.load_ad_btn:
                showAdStatus("Ad loading...");
                if (tSplashAd == null) {
                    tSplashAd = new TSplashAd(this, mSlotId);
                    tSplashAd.setRequestBody(
                            new TAdRequestBody.AdRequestBodyBuild()
                                    .setAdListener(new TAdAlliance(this)).build());
                    tSplashAd.setOnSkipListener(new TOnSkipListener());
                }
                tSplashAd.loadAd();
                break;
            case R.id.show_ad_btn:
                if (tSplashAd != null && tSplashAd.hasAd()) {
                    tSplashAd.showAd(adView,sceneToken);
                } else {
                    ToastUtil.showLongToast("Ad has expired");
                }
                AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> showAd:");
                break;
        }
    }

    // 监听跳过动作
    private class TOnSkipListener implements OnSkipListener {
        @Override
        public void onClick() {
            goToHome();
            SplashAdActivity.this.finish();
            ToastUtil.showLongToast("skip button click");
        }

        @Override
        public void onTimeReach() {
            goToHome();
            ToastUtil.showLongToast("time reach");
        }
    }

    // 广告监听器，监听广告的请求、填充、展示、点击、异常、关闭动作的回调
    private static class TAdAlliance extends TAdListener {
        WeakReference<SplashAdActivity> weakReference;

        private boolean isCloseAd;

        TAdAlliance(SplashAdActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad() {
            // Request success
            super.onLoad();
            if (null == weakReference.get()) {
                return;
            }
            weakReference.get().showAdStatus("Get success");
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            // Request failed
            AdLogUtil.Log().w(ComConstants.AD_FLOW, "SplashAdActivity --> errorCode:" + errorCode.toString());
            weakReference.get().showAdStatus("Ad failed to load Reason for failure: " + errorCode.getErrorMessage());
        }

        @Override
        public void onShow(int source) {
            // Called when an ad is displayed
            isCloseAd = false;
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> showAd:");
            weakReference.get().showAdStatus("Ad display");
        }

        @Override
        public void onClicked(int source) {
            // Called when an ad is clicked
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> ad click");
            weakReference.get().showAdStatus("Clicking on the ad");
        }

        @Override
        public void onClosed(int source) {
            // Called when an ad close
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> ad close");
            weakReference.get().showAdStatus("Ad close");
            weakReference.get().goToHome();
            isCloseAd = true;
            // 注意： 请选择合适的时机调用tSplashAd destroy 方法 否则容易产生内存泄漏
        }
        @Override
        public void onShowError(TAdErrorCode errorCode) {
            // Called when an ad show failed
        }
    }
}
