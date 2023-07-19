package com.hisavana.ssp.util;

import android.content.res.Resources;
import android.util.Log;

import com.hisavana.common.bean.TAdErrorCode;
import com.hisavana.common.bean.TAdNativeInfo;
import com.hisavana.common.bean.TAdRequestBody;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.constant.NativeContextMode;
import com.hisavana.common.interfacz.TAdListener;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.ad.TAdNativeView;
import com.hisavana.mediation.ad.TNativeAd;
import com.hisavana.mediation.ad.ViewBinder;
import com.hisavana.ssp.R;
import com.transsion.core.CoreUtil;
import com.transsion.core.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载native广告
 */
public class AdLoader {
    private TNativeAd tNativeAd;
    private TAdNativeInfo mNativeInfo = null;
    private TAdNativeView nativeView;
    private List<TAdNativeInfo> mNativeInfos = new ArrayList<>();

    public static AdLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final AdLoader INSTANCE = new AdLoader();
    }

    public void loadAd(boolean isPreload, boolean isPreloadNoFillOpen) {
        if (tNativeAd == null) {
            tNativeAd = new TNativeAd(CoreUtil.getContext(), "native_1");
            tNativeAd.setRequestBody(creatBodyRequest());
        }
        if (isPreload) {
            tNativeAd.setRequestBody(createPreloadRequest(isPreloadNoFillOpen));
            tNativeAd.preload();
        } else {
            tNativeAd.setRequestBody(creatBodyRequest());
            long a = System.currentTimeMillis();
            tNativeAd.loadAd();
            Log.d("fangxuhui","广告加载时常 ------》"+(System.currentTimeMillis()-a));
        }
    }

    public void showAd(TAdNativeView nativeView) {
        if (tNativeAd != null && mNativeInfos != null && mNativeInfos.size() > 0) {
            inflateView(mNativeInfos.get(0), nativeView);
        }
    }

    private TAdRequestBody creatBodyRequest() {
        return new TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(new TAdAlliance())
                .build();
    }

    private TAdRequestBody createPreloadRequest(boolean isPreloadNoFillOpen) {
        if (isPreloadNoFillOpen) {
            return new TAdRequestBody.AdRequestBodyBuild()
                    .build();
        }
        return new TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(new TAdAlliance())
                .build();
    }

    /**
     * 装载 广告View
     *
     * @param adNativeInfo 广告信息
     */
    private void inflateView(TAdNativeInfo adNativeInfo, TAdNativeView nativeView) {
        if (adNativeInfo == null) return;
        if (mNativeInfo != null) {
            mNativeInfo.release();
        }
        mNativeInfo = adNativeInfo;
        this.nativeView = nativeView;
        // 判断广告过期
        if (adNativeInfo.isExpired()) {
            ToastUtil.showLongToast("广告过期了");
        }
        Log.i("ssp", "img:" + adNativeInfo.isImageValid() + " icon:" + adNativeInfo.isIconValid());
        Resources resources = nativeView.getContext().getResources();
        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_install)
                .titleId(R.id.native_ad_title)
                .iconId(R.id.native_ad_icon)
            //    .callToActionId(R.id.call_to_action)
                .descriptionId(R.id.native_ad_body)
                .mediaId(R.id.coverview)
                .sponsoredId(R.id.sponsored)
                .ratingId(R.id.rating)
                .priceId(R.id.price)
                .storeMarkView(R.id.store_mark_view)
                .likesId(R.id.likes)
                .downloadsId(R.id.downloads)
                .actionIds(R.id.call_to_action)
                .adChoicesView(R.id.adChoicesView)
                .adCloseView(R.id.adCloseView)
                .contextMode(NativeContextMode.NORMAL)
                .build();
        if (adNativeInfo.isExpired()) {
            AdLogUtil.Log().d("NativeAdActivity", "过期了");
        } else {
            tNativeAd.bindNativeView(nativeView, adNativeInfo, viewBinder);
        }
    }
    /**
     * 广告加载回调
     */
    private class TAdAlliance extends TAdListener {

        @Override
        public void onLoad(List<TAdNativeInfo> tAdNativeInfos, int source) {
            mNativeInfos.clear();
            mNativeInfos.addAll(tAdNativeInfos);
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onLoad，source" + source + "，TAdNativeInfo is:=" + tAdNativeInfos.toString());
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onError" + errorCode.getErrorCode() + "," + errorCode.getErrorMessage());
        }

        @Override
        public void onStart(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onStart，source" + source);
        }

        @Override
        public void onShow(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onShow，source" + source);
        }

        @Override
        public void onClicked(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClicked，source" + source);
        }

        @Override
        public void onClosed(int source) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClosed，source" + source);
            destroyShow();
        }

        @Override
        public void onClosed(TAdNativeInfo tAdNativeInfo) {
            super.onClosed(tAdNativeInfo);
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClosed");
            if (tAdNativeInfo == mNativeInfo) {
                destroyShow();
            }
        }
    }

    public void destroy() {
        if (tNativeAd != null) {
            tNativeAd.destroy();
            tNativeAd = null;
        }
        if (mNativeInfos != null && !mNativeInfos.isEmpty()) {
            for (TAdNativeInfo in : mNativeInfos) {
                if (in != null) {
                    in.destroyAd();
                }
            }
        }
    }

    public void destroyShow() {
        if (mNativeInfo != null) {
            mNativeInfo.release();
        }
        if (nativeView != null) {
            nativeView.release();
        }
        mNativeInfo = null;
        nativeView = null;
    }
}
