package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_NATIVE;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_NATIVE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;

import com.cloud.hisavana.sdk.api.view.MediaView;
import com.cloud.sdk.commonutil.widget.TranCircleImageView;
import com.hisavana.common.bean.AdNativeInfo;
import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdNativeInfo;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.constant.NativeContextMode;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TAdNativeView;
import com.hisavana.mediation.ad.TMediaView;
import com.hisavana.mediation.ad.TNativeAd;

import com.hisavana.mediation.ad.ViewBinder;
import com.hisavana.ssp.R;
import com.hisavana.ssp.util.DemoConstants;
import com.transsion.core.utils.ScreenUtil;
import com.transsion.core.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * @author peng.sun
 * @data 2017/7/5
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class NativeAdActivity extends BaseActivity implements View.OnClickListener {
    //改变加载广告，布局宽度，微调样式
    private Button loadBtn,  show_ad;
    private boolean is_change_style;
    private TNativeAd tNativeAd;
    private EditText slotidetv;
    private TAdNativeView nativeView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TAdNativeInfo mNativeInfo = null;
    private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_NATIVE : SLOT_ID_NATIVE;


    // =============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);

        tvADStatus = findViewById(R.id.tvADStatus);
        showAdStatus("Ready to load ads");

        // 创建一个 加载器
        tNativeAd = new TNativeAd(this, mSlotId);
        tNativeAd.setRequestBody(creatBodyRequest());

        nativeView = findViewById(R.id.native_layout);
        loadBtn = this.findViewById(R.id.load_ad);
        loadBtn.setOnClickListener(this);

        show_ad = this.findViewById(R.id.show_ad);
        show_ad.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (tNativeAd != null) {
            tNativeAd.destroy();
            tNativeAd = null;
        }
        if (mNativeInfo != null) {
            mNativeInfo.release();
        }
        if (nativeView != null) {
            nativeView.release();
        }
        mNativeInfo = null;
        nativeView = null;
        super.onDestroy();
        removeView(slotidetv);
    }

    private void removeView(View v) {
        if (v == null) return;
        ViewParent viewGroup = v.getParent();
        if (viewGroup != null && viewGroup instanceof ViewGroup) {
            ((ViewGroup) viewGroup).removeView(v);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_ad:
                loadAd(false);
                break;
            case R.id.show_ad:
                if (mNativeInfos != null && mNativeInfos.size() > 0) {
                    inflateView(mNativeInfos.get(0));
                    if (mNativeInfos.get(0) instanceof AdNativeInfo) {
                        showAdStatus("MaterialStyle--" + ((AdNativeInfo) mNativeInfos.get(0)).getMaterialStyle());
                    }
                }
                break;

            case R.id.preload:
                loadAd(true);
                break;
        }
    }

    private void loadAd(boolean isPreload) {
        showAdStatus("Ad loading...");

        if (isPreload) {
            tNativeAd.setRequestBody(creatBodyRequest());
            tNativeAd.preload();
        } else {
            loadBtn.setText("Loading Ad");
            tNativeAd.setRequestBody(creatBodyRequest());
            tNativeAd.loadAd();
        }

    }


    // =============================================================================================


    /**
     * Native 广告 里面有多条
     */
    private List<TAdNativeInfo> mNativeInfos = null;


    private TAdRequestBody creatBodyRequest() {
        return new TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(new TAdAlliance(this))
                .build();
    }


    /**
     * 装载 广告View
     *
     * @param adNativeInfo 广告信息
     */
    private void inflateView(TAdNativeInfo adNativeInfo) {
        if (adNativeInfo == null || nativeView == null) return;
        if (mNativeInfo != null) {
            mNativeInfo.release();
        }
        mNativeInfo = adNativeInfo;

        // 判断广告过期
        if (adNativeInfo.isExpired()) {
            ToastUtil.showLongToast("Ad has expired!");
        }

        Log.i("ssp", "img:" + adNativeInfo.isImageValid() + " icon:" + adNativeInfo.isIconValid());
        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_install1).titleId(R.id.native_ad_title)
                .iconId(R.id.native_ad_icon).callToActionId(R.id.call_to_action).descriptionId(R.id.native_ad_body)
                .mediaId(R.id.coverview).sponsoredId(R.id.sponsored).ratingId(R.id.rating).priceId(R.id.price).storeMarkView(R.id.store_mark_view)
                .likesId(R.id.likes).downloadsId(R.id.downloads).actionIds(R.id.call_to_action).contextMode(NativeContextMode.NORMAL)
                .adChoicesView(R.id.adChoicesView).adCloseView(R.id.adCloseView).contextMode(NativeContextMode.NORMAL).build();
        if (adNativeInfo.isExpired()) {
            AdLogUtil.Log().d("NativeAdActivity", "过期了");
        } else {
            tNativeAd.bindNativeView(nativeView, adNativeInfo, viewBinder);
            setAdjustView(adNativeInfo,findViewById(R.id.coverview));
        }

    }

    private void setAdjustView(TAdNativeInfo adNativeInfo,TMediaView tMediaView){
          if(adNativeInfo == null || adNativeInfo.getAdSource() != 0 || tMediaView == null){
              return;
          }
         if(tMediaView.getChildCount() == 0){
             return;
         }
        View childView =  tMediaView.getChildAt(0);
         if(childView instanceof MediaView && ((MediaView) childView).getChildCount()>0 ){
             View  mainView = ((MediaView) childView).getChildAt(0);
             if(mainView instanceof TranCircleImageView){
                 ((TranCircleImageView) mainView).setAdjustViewBounds(true);
             }
         }
    }

    private void closeAd(){
        if (mNativeInfo != null) {
            mNativeInfo.release();
        }
        if (nativeView != null) {
            nativeView.release();
        }
    }

    // =============================================================================================


    /**
     * 广告加载回调
     */
    private static class TAdAlliance extends TAdListener {
        WeakReference<NativeAdActivity> weakReference;

        TAdAlliance(NativeAdActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLoad(List<TAdNativeInfo> tAdNativeInfos, int source) {
            if (weakReference.get() == null) {
                return;
            }

            weakReference.get().loadBtn.setText("Load native");
            weakReference.get().showAdStatus("get success");

            weakReference.get().mNativeInfos = tAdNativeInfos;
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onLoad TAdNativeInfo is:=" + tAdNativeInfos.toString());
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("Ad failed to load Reason for failure: " + errorCode.getErrorMessage());

            weakReference.get().handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().loadBtn.setText("Load native");
                }
            });
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onError");
        }

        @Override
        public void onStart(int source) {
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("onStart");

        }

        @Override
        public void onShow(int source) {
            if (weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("Ad display");
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onShow");

        }

        @Override
        public void onClicked(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClicked");
            weakReference.get().showAdStatus("Clicking on the ad");
        }

        @Override
        public void onClosed(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClosed");
            weakReference.get().showAdStatus("Ad close");
        }

        @Override
        public void onClosed(TAdNativeInfo tAdNativeInfo) {
            super.onClosed(tAdNativeInfo);
          NativeAdActivity activity = weakReference.get();
          if (activity != null){
              activity.closeAd();
          }
        }
    }


}
