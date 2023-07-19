package com.hisavana.ssp.ui;

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
    private RelativeLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        tvADStatus = findViewById(R.id.tvADStatus);
        parentView = findViewById(R.id.root);
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


    // =============================================================================================


    public void loadBannerAd(View view) {
        if(adview == null){
            adview = new TBannerView(this);
            Log.d("fangxuhui","adview == " + adview.hashCode());
            adview.setAdSize(BannerSize.SIZE_320x50);
            adview.setAdUnitId(mSlotId);
            TAdRequestBody tAdRequest = new TAdRequestBody.AdRequestBodyBuild()
                    .setAdListener(new TAdAlliance(this))
                    .build();
            adview.setRequestBody(tAdRequest);
            parentView.addView(adview);
        }
        AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> BannerAdActivity --> start load Banner");
        loadBtn.setText("Loading Banner");
        showAdStatus("ad loading...");
        adview.loadAd();

    }

    private void closeAd(){
        if(adview != null){
             adview.destroy();
            ((ViewGroup) adview.getParent()).removeView(adview);
            adview.removeAllViews();
            adview = null;
        }
    }
    // =============================================================================================


    private static class TAdAlliance extends TAdListener {

        WeakReference<BannerAdActivity> weakReference;

        TAdAlliance(BannerAdActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad(int source) {
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
        public void onStart(int source) {
        }

        @Override
        public void onShow(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onShow");
            weakReference.get().showAdStatus("Ad display");
        }

        @Override
        public void onClicked(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onClicked");
            weakReference.get().showAdStatus("clicked on the ad");
        }

        @Override
        public void onClosed(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "BannerAdActivity --> onClosed");
            weakReference.get().showAdStatus("Ad close");
            weakReference.get().closeAd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adview != null){
            adview.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adview != null){
            adview.pause();
        }
    }
}
