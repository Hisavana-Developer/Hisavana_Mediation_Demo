package com.hisavana.ssp.video;

import static com.hisavana.common.constant.ComConstants.VIDEO_TAG;
import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_VIDEO;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_VIDEO;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TVideoAd;
import com.hisavana.ssp.R;
import com.hisavana.ssp.ui.BaseActivity;
import com.hisavana.ssp.util.DemoConstants;

import java.lang.ref.WeakReference;

/**
 * 聚合 激励视频
 */
public class ADMediationVideoActivity extends BaseActivity {

    /**
     * 记载激励视频的
     */
    private TVideoAd tVideoAd;
    private boolean loading = false;
    private boolean showing = false;
    private Button load;
    private Button preload;
    private Button show;
    private CountDownTimer countDownTimer;
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_VIDEO : SLOT_ID_VIDEO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Video");
        setContentView(R.layout.activity_admediation_video);

        tvADStatus = findViewById(R.id.tvADStatus);
        load = findViewById(R.id.load);
        preload = findViewById(R.id.preload);
        show = findViewById(R.id.show);
        showAdStatus("准备就绪可以加载广告了");
    }

    @Override
    protected void onDestroy() {
        if (null != tVideoAd) {
            tVideoAd.destroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onDestroy();
    }

    /**
     * 加载广告
     */
    public void onLoadClick(View view) {
        loadAd(false);

    }

    public void preloadAd(View view){
        loadAd(true);
        preload.setEnabled(false);

    }

    private void loadAd(boolean isPreload){
        if (!loading) {
            loading = true;
            TAdRequestBody tAdRequest = new TAdRequestBody.AdRequestBodyBuild()
                    .setAdListener(new TAdAlliance(this))
                    .build();

            tVideoAd = new TVideoAd(this,mSlotId);
            tVideoAd.setRequestBody(tAdRequest);
            if(isPreload){
                tVideoAd.preload();
            }else{
                load.setTextColor(Color.GRAY);
                showAdStatus("广告加载中...");
                tVideoAd.loadAd();
            }
        }
    }

    /**
     * 显示广告
     */
    public void onShowClick(View view) {
        if (!showing && loading) {
            showing = true;
            if (tVideoAd != null && tVideoAd.hasAd()) {
                tVideoAd.show(this);
                startDelayTimer();
            }
        }
    }

    private void startDelayTimer() {
        countDownTimer = new CountDownTimer(25 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                load.setText(millisUntilFinished / 1000 + "s后可再次点击");
                load.setTextColor(Color.GRAY);
                show.setText(millisUntilFinished / 1000 + "s后可再次点击");
                show.setTextColor(Color.GRAY);
            }

            @Override
            public void onFinish() {
                load.setText("LOADAD");
                load.setTextColor(Color.BLACK);
                show.setText("SHOW");
                show.setTextColor(Color.BLACK);
                showing = false;
                loading = false;
            }
        };
        countDownTimer.start();
    }

    // =============================================================================================

    /**
     * 广告会回调
     */
    private static class TAdAlliance extends TAdListener {

        WeakReference<ADMediationVideoActivity> weakReference;

        TAdAlliance(ADMediationVideoActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("get ad success ");
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> onAllianceLoad");
        }

        @Override
        public void onError(TAdErrorCode errorCode) {

            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().loading = false;
            weakReference.get().load.setTextColor(Color.BLACK);
            weakReference.get().showAdStatus("广告加载失败 失败的原因：" + errorCode.getErrorMessage() + "，errorcode：" + errorCode.getErrorCode());
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> onAllianceError");
        }

        @Override
        public void onShow(int source) {
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("广告展示 source "+source);
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> onShow");
        }

        @Override
        public void onClicked(int source) {
            if (weakReference.get() == null) {
                return;
            }
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> onClicked");
            weakReference.get().showAdStatus("点击了广告 source "+source);
        }

        @Override
        public void onClosed(int source) {
            if (weakReference.get() == null) {
                return;
            }
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> onClosed");
            weakReference.get().showAdStatus("广告关闭 source "+source);
        }

        @Override
        public void onRewarded() {
            super.onRewarded();
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("获取激励奖励");
            AdLogUtil.Log().d(VIDEO_TAG, "ADMediationVideoActivity --> 获取激励奖励");
        }
    }
}
