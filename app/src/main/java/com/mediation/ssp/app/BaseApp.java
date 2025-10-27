package com.mediation.ssp.app;

import static com.hisavana.common.constant.ComConstants.SDK_INIT;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.cloud.sdk.commonutil.util.HSSharedPreferencesUtil;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.utils.AdLogUtil;
import com.mediation.ssp.R;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.transsion.core.utils.SharedPreferencesUtil;

/**
 * @author peng.sun
 * @data 2017/7/5
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class BaseApp extends Application {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AdLogUtil.Log().d(ComConstants.SDK_INIT, "BaseApp --> onCreate");
        HSSharedPreferencesUtil.bindApplication(this);
        initWebPath();
    }

    private void initWebPath(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String processName = "";
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                    if (processInfo.pid == android.os.Process.myPid()) {
                        processName =  processInfo.processName;
                    }
                }
                if (!getPackageName().equals(processName)) {
                    //判断不等于默认进程名称
                    WebView.setDataDirectorySuffix(processName);
                }
            }
        }catch (Exception e){
            AdLogUtil.Log().d(SDK_INIT,"initWebPath "+ Log.getStackTraceString(e));
        }
    }
}
