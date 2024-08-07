package com.hisavana.ssp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hisavana.ssp.R;
import com.hisavana.ssp.video.ADMediationVideoActivity;

public class NativeMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mNativeAd;
    private RelativeLayout mNativeListAd;
    private RelativeLayout mNativeIconAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Native");
        setContentView(R.layout.activity_native_main);

        mNativeAd = findViewById(R.id.nativeBtn);
        mNativeListAd = findViewById(R.id.nativeListBtn);
        mNativeIconAd = findViewById(R.id.native_icon_Btn);

        mNativeAd.setOnClickListener(this);
        mNativeListAd.setOnClickListener(this);
        mNativeIconAd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nativeBtn:
                startActivity(new Intent(this, NativeAdActivity.class));
                break;
            case R.id.nativeListBtn:
                startActivity(new Intent(this, NativeAdInListActivity.class));
                break;
            case R.id.native_icon_Btn:
                startActivity(new Intent(this, IconAdActivity.class));
                break;
        }
    }
}