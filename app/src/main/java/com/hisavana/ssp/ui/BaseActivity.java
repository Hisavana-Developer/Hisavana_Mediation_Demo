package com.hisavana.ssp.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
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
        // 启用ActionBar的返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        initListener();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 返回到上一个Activity
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    protected void initListener() {
    }
}
