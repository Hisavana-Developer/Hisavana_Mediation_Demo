package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_SPLASH;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_SPLASH;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdNativeInfo;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.interfacz.OnSkipListener;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TSplashAd;
import com.hisavana.mediation.ad.TSplashView;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;
import com.transsion.core.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 开屏广告Demo
 */
public class SplashAdActivity extends BaseActivity {

    /**
     * 开屏广告 View
     */
    private TSplashView adView;
    private TSplashAd tSplashAd;
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_SPLASH : SLOT_ID_SPLASH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ad);

        tvADStatus = findViewById(R.id.tvADStatus);
        showAdStatus("Ready to load ads");

        adView = findViewById(R.id.splash_ad);

    }

    public void onLoadSplah(View view) {
        showAdStatus("Ad loading...");
        if (tSplashAd == null) {
            tSplashAd = new TSplashAd(this, mSlotId);
            tSplashAd.setRequestBody(
                    new TAdRequestBody.AdRequestBodyBuild()
                            .setAdListener(new TAdAlliance(this)).build());
            tSplashAd.setOnSkipListener(new TOnSkipListener());
        }
        tSplashAd.loadAd();
    }

    public void onShowSplash(View view) {
        if (tSplashAd.isReady()) {
            tSplashAd.showAd(adView);
        } else {
            ToastUtil.showLongToast("Ad has expired");
        }
        AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> showAd:");
    }

    public void onPreloadSplah(View view) {
        showAdStatus("Ad loading...");
        if (tSplashAd == null) {
            tSplashAd = new TSplashAd(this, mSlotId);
            tSplashAd.setRequestBody(
                    new TAdRequestBody.AdRequestBodyBuild()
                            .setAdListener(new TAdAlliance(this)).build());
            tSplashAd.setOnSkipListener(new TOnSkipListener());
        }
        tSplashAd.preload();
    }

    /**
     * 检查 开屏广告 是否存在缓存
     */
    public void onClickCheckCache(View view) {
        String hasCache;
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
        if (tSplashAd != null) {
            tSplashAd.destroy();
            tSplashAd = null;
        }
    }

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

    private static class TAdAlliance extends TAdListener {
        WeakReference<SplashAdActivity> weakReference;

        private boolean isCloseAd;

        TAdAlliance(SplashAdActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onStart(int source) {
            super.onStart(source);
            ToastUtil.showToast("ad load");
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> ad load");
        }

        @Override
        public void onLoad(List<TAdNativeInfo> tAdNativeInfos, int source) {
            super.onLoad(tAdNativeInfos, source);
        }

        @Override
        public void onLoad(int source) {
            super.onLoad(source);
            if (null == weakReference.get()) {
                return;
            }
            weakReference.get().showAdStatus("Get success");
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            AdLogUtil.Log().w(ComConstants.AD_FLOW, "SplashAdActivity --> errorCode:" + errorCode.toString());
            weakReference.get().showAdStatus("Ad failed to load Reason for failure: " + errorCode.getErrorMessage());
        }

        @Override
        public void onShow(int source) {
            isCloseAd = false;
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> showAd:");
            weakReference.get().showAdStatus("Ad display");
        }

        @Override
        public void onClicked(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> ad click");
            weakReference.get().showAdStatus("Clicking on the ad");
        }

        @Override
        public void onClosed(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "SplashAdActivity --> ad close");
            weakReference.get().showAdStatus("Ad close");
            weakReference.get().goToHome();
            isCloseAd = true;

        }
    }


}
