package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.LOG_TAG;
import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_BANNER;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_BANNER;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.BannerSize;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TBannerView;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;

import java.lang.ref.WeakReference;

/**
 * @author: peng.sun
 * @date: 2018/7/11 10:41
 * ==================================
 * Copyright (c) 2018 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class BannerAdActivity extends BaseActivity {

    private TBannerView adview;
    private Button loadBtn;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_BANNER : SLOT_ID_BANNER;
    private LinearLayout mLlBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        tvADStatus = findViewById(R.id.tvADStatus);
        mLlBanner = findViewById(R.id.ll_banner);
        loadBtn = findViewById(R.id.load_banner);
        showAdStatus("Ready to load ads");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adview != null) {
            adview.destroy();
            adview.removeAllViews();
        }
        adview = null;
        handler.removeCallbacksAndMessages(null);
    }

    public void loadBannerAd(View view) {
        if (adview == null) {
            adview = new TBannerView(this);
            Log.d(LOG_TAG, "adview == " + adview.hashCode());
            adview.setAdSize(BannerSize.SIZE_320x50);
            //广告位ID
            adview.setAdUnitId(mSlotId);

            /**
             * 可选项
             * 对于需要统计到达广告场景的应用，可自行设置场景值
             * 主要目的是统计当前广告场景利用率，第一个参数自定义场景名称，第二个参数广告数量
             */
            adview.enterScene("banner_scene_name", 1);
            TAdRequestBody tAdRequest = new TAdRequestBody.AdRequestBodyBuild()
                    .setAdListener(new TAdAlliance(this))
                    .build();
            adview.setRequestBody(tAdRequest);
            mLlBanner.addView(adview);
        }
        AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> BannerAdActivity --> start load Banner");
        loadBtn.setText("Loading Banner");
        showAdStatus("ad loading...");
        adview.loadAd();

    }

    private void closeAd() {
        if (adview != null) {
            adview.destroy();
            ((ViewGroup) adview.getParent()).removeView(adview);
            adview.removeAllViews();
            adview = null;
        }
    }

    // 广告监听器，监听广告的请求、填充、展示、点击、异常、关闭动作的回调
    private static class TAdAlliance extends TAdListener {
        WeakReference<BannerAdActivity> weakReference;

        TAdAlliance(BannerAdActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad() {
            // Request success
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onLoad");
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("get success");

            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().loadBtn.setText("Load Banner");
                }
            });
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            // Request failed
            if (weakReference.get() == null || null == errorCode) {
                return;
            }
            weakReference.get().showAdStatus("Ad failed to load Reason for failure: " + errorCode.getErrorMessage());

            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().loadBtn.setText("Load Banner");
                }
            });
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onError errorCode=" + errorCode.getErrorMessage());
        }

        @Override
        public void onShow(int source) {
            // Called when an ad is displayed
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onShow");
            weakReference.get().showAdStatus("Ad display");
        }

        @Override
        public void onClicked(int source) {
            // Called when an ad is clicked
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onClicked");
            weakReference.get().showAdStatus("clicked on the ad");
        }

        @Override
        public void onClosed(int source) {
            // Called when an ad close
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onClosed");
            weakReference.get().showAdStatus("Ad close");
            //适当的时机释放 destroy
            weakReference.get().closeAd();
        }

        @Override
        public void onShowError(TAdErrorCode errorCode) {
            // Called when an ad show failed
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adview != null) {
            adview.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adview != null) {
            adview.pause();
        }
    }
}
