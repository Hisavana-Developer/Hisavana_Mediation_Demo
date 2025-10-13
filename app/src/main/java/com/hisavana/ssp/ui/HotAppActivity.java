package com.hisavana.ssp.ui;

import static com.hisavana.ssp.util.DemoConstants.SLOT_ID_HOT_APP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.transsion.core.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class HotAppActivity extends BaseActivity implements View.OnClickListener {
    private Button loadBtn, show_ad;
    private TNativeAd tNativeAd;
    private EditText slotidetv;
    private RecyclerView nativeRv;
    private LinearLayoutManager linearLayoutManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String mSlotId = SLOT_ID_HOT_APP;

    private HotAdapter hotAdapter;
    private List<TAdNativeInfo> nativeInfos = new ArrayList<>();
    private String sceneToken;

    // =============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_app);
        tvADStatus = findViewById(R.id.tvADStatus);
        showAdStatus("Ready to load ad");

        // 创建一个 加载器
        tNativeAd = new TNativeAd(this, SLOT_ID_HOT_APP);
        mSlotId = SLOT_ID_HOT_APP;
        tNativeAd.setRequestBody(creatBodyRequest());
        nativeRv = findViewById(R.id.rv_native);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        nativeRv.setLayoutManager(linearLayoutManager);
        hotAdapter = new HotAdapter();
        nativeRv.setAdapter(hotAdapter);
        slotidetv = findViewById(R.id.placement_id);
        slotidetv.setText(SLOT_ID_HOT_APP);
        slotidetv.setEnabled(false);
        loadBtn = this.findViewById(R.id.load_ad);
        loadBtn.setOnClickListener(this);
        show_ad = this.findViewById(R.id.show_ad);
        show_ad.setOnClickListener(this);
        sceneToken = tNativeAd.enterScene("hot_scene_id",1);
    }

    @Override
    protected void onDestroy() {
        if (tNativeAd != null) {
            tNativeAd.destroy();
            tNativeAd = null;
        }
        nativeInfoRelease();
        //    nativeView = null;
        super.onDestroy();
        removeView(slotidetv);
    }

    private void nativeInfoRelease(){
        for (TAdNativeInfo nativeInfo : nativeInfos) {
            if(nativeInfo != null){
                nativeInfo.destroyAd();
            }
        }
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
                List<TAdNativeInfo> adList = tNativeAd.getNativeAdInfo();
                if(adList != null && adList.size()>0){
                    nativeInfos.addAll(adList);
                    hotAdapter.setNativeInfos(adList);
                }
                break;
        }
    }

    public void preloadAd(View view) {
        loadAd(true);
    }

    private void loadAd(boolean isPreload) {
        showAdStatus("Ad loading...");

        String slotid = (slotidetv != null && !TextUtils.isEmpty(slotidetv.getText()))
                ? slotidetv.getText().toString() : SLOT_ID_HOT_APP;

        if (!TextUtils.equals(slotid, mSlotId)) {
            if (tNativeAd != null) {
                tNativeAd.destroy();
            }
            tNativeAd = new TNativeAd(this, slotid);
            mSlotId = slotid;

        }
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

    private TAdRequestBody creatBodyRequest() {
        return new TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(new TAdAlliance(this))
                .build();
    }

    // =============================================================================================


    /**
     * 广告加载回调
     */
    private static class TAdAlliance extends TAdListener {
        WeakReference<HotAppActivity> weakReference;

        TAdAlliance(HotAppActivity activity) {
            this.weakReference = new WeakReference(activity);
        }

        @Override
        public void onLoad() {
            if (weakReference.get() == null) {
                return;
            }

            weakReference.get().loadBtn.setText("Load native");
            weakReference.get().showAdStatus("get success");
            weakReference.get().nativeInfoRelease();
            weakReference.get().hotAdapter.clear();
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
        public void onNativeFeedShow(int i, @Nullable TAdNativeInfo tAdNativeInfo) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onNativeFeedShow");
        }

        @Override
        public void onNativeFeedClicked(int i, @Nullable TAdNativeInfo tAdNativeInfo) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onNativeFeedClicked");
        }

        @Override
        public void onClosed(@Nullable TAdNativeInfo tAdNativeInfo) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onClosed");
        }

        @Override
        public void onShowError(@Nullable TAdErrorCode tAdErrorCode) {
            AdLogUtil.Log().d(ComConstants.AD_FLOW, "NativeAdActivity --> onShowError");
        }

        @Override
        public void onShow(int source) {
            // Nothing
        }

        @Override
        public void onClicked(int source) {
            // Nothing
        }

        @Override
        public void onClosed(int source) {
            // Nothing
        }
    }

    public class HotAdapter extends RecyclerView.Adapter{

        private List<TAdNativeInfo> nativeInfos = new ArrayList();

        public void setNativeInfos(List<TAdNativeInfo> nativeInfos) {
            if(nativeInfos!=null){
                Iterator<TAdNativeInfo> iterator = nativeInfos.iterator();
                while(iterator.hasNext()){
                    TAdNativeInfo next = iterator.next();
                    if(next == null || next.isExpired()){
                        iterator.remove();
                    }
                }
                this.nativeInfos = nativeInfos;
                if(nativeInfos.isEmpty()){
                    ToastUtil.showLongToast("Ad expired");
                }
                notifyDataSetChanged();
            }
        }

        public void clear(){
           if(nativeInfos != null){
               nativeInfos.clear();
           }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hot_layout, viewGroup, false);
            HotHolder hotHolder = new HotHolder(itemView);
            return hotHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_hotapp_install)
                    .iconId(R.id.native_ad_icon).titleId(R.id.native_ad_title).contextMode(NativeContextMode.LIST).build();
            tNativeAd.bindNativeView(((HotHolder)viewHolder).native_layout, nativeInfos.get(i), viewBinder,sceneToken);
        }

        @Override
        public int getItemCount() {
            return nativeInfos == null?0:nativeInfos.size();
        }
    }

    public class HotHolder extends RecyclerView.ViewHolder{

        public TAdNativeView native_layout;
        public HotHolder(@NonNull View itemView) {
            super(itemView);
            native_layout = itemView.findViewById(R.id.native_layout);
        }
    }
}


