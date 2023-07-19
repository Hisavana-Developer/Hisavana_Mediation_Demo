package com.hisavana.ssp.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 日志输出
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 打印广告的请求状态的  子类实例化
     */
    public TextView tvADStatus;
    private StringBuilder sb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sb = new StringBuilder();
    }

    /**
     * 日志可视化
     *
     * @param msg
     */
    public void showAdStatus(String msg) {
        if (null != sb && null != tvADStatus) {
            sb.append(msg).append("\n");
            tvADStatus.setText(sb.toString());
        }
    }
}
