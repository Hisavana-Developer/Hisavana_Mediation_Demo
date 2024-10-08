package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_INTERSTITIAL;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_INTERSTITIAL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TInterstitialAd;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;
import com.transsion.core.utils.ToastUtil;

import java.lang.ref.WeakReference;

public class InterstitialActivity extends BaseActivity implements View.OnClickListener {

    private TInterstitialAd mTInterstitialAd;
    private Button loadBtn;
    private Button showButton;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_INTERSTITIAL : SLOT_ID_INTERSTITIAL;

    private boolean ifSaveInstanceState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        setTitle("Interstitial");
        tvADStatus=findViewById(R.id.tvADStatus);
        showAdStatus("Ready to load ads");

        if (mTInterstitialAd == null) {
            mTInterstitialAd = new TInterstitialAd(this, mSlotId);
        }
        loadBtn = findViewById(R.id.btn_load_interstitial);
        showButton = findViewById(R.id.tAdInterstitial_show);
        showButton.setOnClickListener(this);
        loadBtn.setOnClickListener(this);
        ifSaveInstanceState = false;
        creatRequest();
    }


    private void creatRequest() {
        TAdRequestBody tAdRequest = new TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(new TAdAlliance(this))
                .build();
        mTInterstitialAd.setRequestBody(tAdRequest);
    }

    private void loadAd(boolean isPreload){
        showAdStatus("Ad loading...");
        if(isPreload){
            mTInterstitialAd.preload();
        }else{
            // 加载广告后在设置的等待时间内将最优广告回调返回
            creatRequest();
            mTInterstitialAd.loadAd();
            loadBtn.setText("Loading Interstitial");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tAdInterstitial_show:
                if (mTInterstitialAd != null){
                    if (!mTInterstitialAd.hasAd()){
                        ToastUtil.showLongToast("Ad expired");
                    }else{
                        mTInterstitialAd.show(InterstitialActivity.this);
                        /**
                         * 可选项
                         * 对于需要统计到达广告场景的应用，可自行设置场景值
                         * 主要目的是统计当前广告场景利用率，第一个参数自定义场景名称，第二个参数广告数量
                         */
                         // String sceneToken = mTInterstitialAd.enterScene("interstitial_scene_name", 1);
                         // if (mTInterstitialAd != null && mTInterstitialAd.hasAd()) {
                         //  mTInterstitialAd.show(context, sceneToken);
                         //}
                    }
                }
                break;
            case R.id.btn_load_interstitial:
                // 加载广告
                loadAd(false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //避免横竖屏切换导致广告销毁
        if (null != outState) {
            ifSaveInstanceState = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出广告场景后，请销毁该广告对象。
        if (mTInterstitialAd != null && !ifSaveInstanceState) {
            mTInterstitialAd.destroy();
            mTInterstitialAd = null;
        }
        handler.removeCallbacksAndMessages(null);
    }


    // 广告监听器，监听广告的请求、填充、展示、点击、异常、关闭动作的回调
    private static class TAdAlliance extends TAdListener {
        WeakReference<InterstitialActivity> weakReference;

        TAdAlliance(InterstitialActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad() {
            // Request success
            if (weakReference.get() == null) {
                return;
            }

            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().loadBtn.setText("Load Interstitial");
                    weakReference.get().showAdStatus("get success");
                }
            });
            AdLogUtil.Log().d(ComConstants.AD_FLOW,"InterstitialActivity --> onLoad");
        }

        @Override
        public void onError(final TAdErrorCode errorCode) {
            // Request failed
            if (weakReference.get() == null) {
                return;
            }

            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().loadBtn.setText("Load Interstitial");
                    weakReference.get().showAdStatus("Ad failed to load Reason for failure: "+errorCode.getErrorMessage());
                }
            });
            AdLogUtil.Log().d(ComConstants.AD_FLOW,"InterstitialActivity --> onError");
        }

        @Override
        public void onShow(int source) {
            // Called when an ad is displayed
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().showAdStatus("Ad display");
                }
            });

            AdLogUtil.Log().d(ComConstants.AD_FLOW,"InterstitialActivity --> onShow");
        }

        @Override
        public void onClicked(int source) {
            // Called when an ad is clicked
            AdLogUtil.Log().d(ComConstants.AD_FLOW,"InterstitialActivity --> onClicked");
            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().showAdStatus("clicking on the ad");
                }
            });
        }

        @Override
        public void onClosed(int source) {
            // Called when an ad close
            AdLogUtil.Log().d(ComConstants.AD_FLOW,"InterstitialActivity --> onClosed");
            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().showAdStatus("Ad close");
                }
            });

        }

        @Override
        public void onShowError(TAdErrorCode errorCode) {
            // Called when an ad show failed
        }
    }
}
