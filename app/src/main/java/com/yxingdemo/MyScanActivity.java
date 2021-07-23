package com.yxingdemo;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.yxing.ScanCodeActivity;

/**
 * @author am
 */
public class MyScanActivity extends ScanCodeActivity {

    private AppCompatButton btnOpenFlash;

    private boolean isOpenFlash;

    @Override
    public int getLayoutId() {
        return R.layout.activity_myscan;
    }

    @Override
    public void initData() {
        super.initData();
        btnOpenFlash = findViewById(R.id.btn_openflash);

        btnOpenFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpenFlash = !isOpenFlash;
                setFlashStatus(isOpenFlash);
                btnOpenFlash.setText(isOpenFlash ? "关闭闪光灯" : "打开闪光灯");
            }
        });
    }
}
