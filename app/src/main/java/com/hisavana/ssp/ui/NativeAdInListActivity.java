package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_NATIVE;
import static com.hisavana.ssp.util.DemoConstants.TEST_SLOT_ID_NATIVE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.hisavana.sdk.api.view.MediaView;
import com.cloud.sdk.commonutil.widget.TranCircleImageView;
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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表中的 NativeAd
 */
public class NativeAdInListActivity extends BaseActivity {

    /*** 列表 Item 封装 JavaBean*/
    private final List<ItemBean> list = new ArrayList<>();
    private static final String TAG = "ADSDK_DEMO";
    private static final String NATIVE_TAG = "native_tag";
    private static final int ITEM_TYPE_DEFAULT = 0;
    private static final int ITEM_TYPE_Native = 1;

    private  InnerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_in_list);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
         adapter = new InnerAdapter(list);
        rv.setAdapter(adapter);

        // 加载默认数据
        for (int i = 0; i < 100; i++) {
            if (i == 0 || i % 10 == 0) {
                list.add(new ItemBean(NATIVE_TAG, null, i, this));
            } else {
                list.add(new ItemBean("native demo item " + i, null, i, this));
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 释放资源
        for (ItemBean itemBean : list) {
            if (null == itemBean) {
                continue;
            }
            if (null != itemBean.nativeInfo) {
                itemBean.nativeInfo.release();
            }
            if (itemBean.nativeAd != null) {
                itemBean.nativeAd.destroy();
            }
            if (itemBean.tAdNativeView != null) {
                itemBean.tAdNativeView.removeAllViews();
            }
            itemBean.weakReference = null;
            AdLogUtil.Log().d(TAG, "资源回收");
        }
        list.clear();
        super.onDestroy();
    }

    private void closeAd(TAdNativeInfo tAdNativeInfo){
        int position = -1;
        for (int i = 0; i<list.size(); i++){
            ItemBean item = list.get(i);
            if (item!=null && tAdNativeInfo == item.nativeInfo){
                position = i;
                break;
            }
        }
        if(position>-1){
            list.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    // =============================================================================================


    /**
     * 列表适配器
     */
    static class InnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<ItemBean> itemBeanList;

        InnerAdapter(List<ItemBean> list) {
            if (null == list) {
                itemBeanList = new ArrayList<>();
            } else {
                itemBeanList = list;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == 0) {
                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
                viewHolder = new VHHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ad_layout, viewGroup, false);
                viewHolder = new VHADHolder(itemView);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vhHolder, int position) {
            if (getItemViewType(position) == ITEM_TYPE_Native) {
                ((VHADHolder) vhHolder).setData(itemBeanList.get(position));
            } else {
                ((VHHolder) vhHolder).setData(itemBeanList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return itemBeanList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (TextUtils.equals(NATIVE_TAG, itemBeanList.get(position).getTitle())) {
                return ITEM_TYPE_Native;
            } else {
                return ITEM_TYPE_DEFAULT;
            }
        }

        static class VHHolder extends RecyclerView.ViewHolder {

            private final AppCompatButton btn;

            public VHHolder(@NonNull View itemView) {
                super(itemView);
                btn = itemView.findViewById(R.id.btn);
            }

            public void setData(ItemBean itemBean) {
                btn.setText(itemBean.title);
            }
        }

        static class VHADHolder extends RecyclerView.ViewHolder {

            /***Native 广告容器*/
            TAdNativeView nativeLayout;

            public VHADHolder(@NonNull View itemView) {
                super(itemView);
                nativeLayout = itemView.findViewById(R.id.native_layout);
            }

            public void setData(ItemBean itemBean) {
                itemBean.bindValues(nativeLayout);
            }
        }
    }


    // =============================================================================================


    /**
     * 列表的 JavaBean
     */
    static class ItemBean extends TAdListener implements Serializable {
        private String mSlotId = DemoConstants.IS_DEBUG ? TEST_SLOT_ID_NATIVE : SLOT_ID_NATIVE;
        /***标题*/
        private final String title;
        /***广告信息*/
        private TAdNativeInfo nativeInfo;
        /***广告请求对象*/
        private TNativeAd nativeAd;
        /***列表中的位置*/
        private final int position;
        /***AdView*/
        private TAdNativeView tAdNativeView;
        /***Activity*/
        private WeakReference<NativeAdInListActivity> weakReference;

        public ItemBean(String title, TAdNativeInfo nativeInfo, int position, NativeAdInListActivity activity) {
            this.title = title;
            this.nativeInfo = nativeInfo;
            this.position = position;
            this.weakReference = new WeakReference<>(activity);
        }

        public String getTitle() {
            return title;
        }

        public void bindValues(TAdNativeView tAdNativeView) {
            this.tAdNativeView = tAdNativeView;
            this.tAdNativeView.setTag(position);
            this.tAdNativeView.setVisibility(View.GONE);

            // 加载广告
            if (nativeInfo == null) {
                if (weakReference == null || weakReference.get() == null) {
                    return;
                }
                nativeAd = new TNativeAd(weakReference.get(), mSlotId);
                nativeAd.setRequestBody(new TAdRequestBody.AdRequestBodyBuild()
                        .setAdListener(this).build());
                nativeAd.loadAd();
            } else {
                bindAd();
            }
        }

        private void bindAd() {
            tAdNativeView.setVisibility(View.VISIBLE);
            nativeAd.bindNativeView(tAdNativeView, nativeInfo, getBinder());
            AdLogUtil.Log().d(TAG, "------->bindValues --> bindNativeView(),position == " + position + "/" + nativeInfo.getAdId());
            setAdjustView(nativeInfo, tAdNativeView.findViewById(R.id.coverview));
        }

        private ViewBinder getBinder() {
            return new ViewBinder.Builder(R.layout.native_install1)
                    .titleId(R.id.native_ad_title)
                    .iconId(R.id.native_ad_icon).
                    callToActionId(R.id.call_to_action)
                    .descriptionId(R.id.native_ad_body)
                    .contextMode(NativeContextMode.LIST)
                    .mediaId(R.id.coverview)
                    .adChoicesView(R.id.adChoicesView)
                    .adCloseView(R.id.adCloseView)
                    .storeMarkView(R.id.store_mark_view)
                    .build();
        }

        private void setAdjustView(TAdNativeInfo adNativeInfo, TMediaView tMediaView) {
            if (adNativeInfo == null || adNativeInfo.getAdSource() != 0 || tMediaView == null) {
                return;
            }
            if (tMediaView.getChildCount() == 0) {
                return;
            }
            View childView = tMediaView.getChildAt(0);
            if (childView instanceof MediaView && ((MediaView) childView).getChildCount() > 0) {
                View mainView = ((MediaView) childView).getChildAt(0);
                if (mainView instanceof TranCircleImageView) {
                    ((TranCircleImageView) mainView).setAdjustViewBounds(true);
                }
            }
        }


        // -----------------------------------------------------------------------------------------


        @Override
        public void onLoad(List<TAdNativeInfo> tAdNativeInfos, int source) {
            AdLogUtil.Log().d(TAG, "------->TAdAlliance --> onLoad position ==== " + position);

            if (nativeInfo == null && tAdNativeInfos != null && !tAdNativeInfos.isEmpty()) {
                nativeInfo = tAdNativeInfos.get(0);
                bindAd();
            }

            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("Get success = " + position);
        }

        @Override
        public void onError(TAdErrorCode errorCode) {
            AdLogUtil.Log().e(TAG, "TAdAlliance --> onError position = " + position);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("i = " + position + " ,Ad failed to load Reason for failure:" + errorCode.getErrorMessage());
        }

        @Override
        public void onShow(int source) {
            AdLogUtil.Log().d(TAG, "TAdAlliance --> onShow，position = " + position);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("Ad display,  i = " + position);
        }

        @Override
        public void onClicked(int source) {
            AdLogUtil.Log().d(TAG, "TAdAlliance --> onClicked position = " + position);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            weakReference.get().showAdStatus("Clicking on the ad, i = " + position);
        }

        @Override
        public void onClosed(int source) {
            AdLogUtil.Log().d(TAG, "TAdAlliance --> onClosed position = " + position);
            if (weakReference == null || (weakReference.get() == null)) {
                return;
            }
            weakReference.get().showAdStatus("Ad  close， i = " + position);
        }

        @Override
        public void onClosed(TAdNativeInfo tAdNativeInfo) {
            super.onClosed(tAdNativeInfo);
            if (weakReference == null || (weakReference.get() == null) || tAdNativeInfo == null) {
                return;
            }
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "TAdAlliance --> onClosed");
            weakReference.get().showAdStatus("Ad close");
            weakReference.get().closeAd(tAdNativeInfo);
            if (null != nativeInfo) {
                nativeInfo.release();
            }
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            if (tAdNativeView != null) {
                tAdNativeView.removeAllViews();
            }
        }
    }

}
