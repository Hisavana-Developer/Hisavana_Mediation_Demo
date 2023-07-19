package com.hisavana.ssp.app;

import static com.hisavana.common.constant.ComConstants.SDK_INIT;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.cloud.hisavana.sdk.config.AdxServerConfig;
import com.cloud.sdk.commonutil.control.AdxPreferencesHelper;
import com.hisavana.common.constant.ComConstants;
import com.hisavana.common.utils.AdLogUtil;
import com.hisavana.mediation.config.TAdManager;
import com.hisavana.ssp.util.DemoConstants;
import com.transsion.core.utils.SharedPreferencesUtil;

/**
 * @author peng.sun
 * @data 2017/7/5
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class BaseApp extends Application {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AdLogUtil.Log().d(ComConstants.SDK_INIT, "BaseApp --> onCreate");
        SharedPreferencesUtil.bindApplication(this);
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
