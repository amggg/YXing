package com.yxingdemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.yxing.ScanCodeActivity;
import com.yxing.ScanCodeConfig;
import com.yxing.def.ScanStyle;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author apple
 */
public class MainActivity extends AppCompatActivity {

    private RadioGroup rgParent;
    private AppCompatButton btnScan;
    private AppCompatTextView tvCode;
    private AppCompatImageView ivCode;
    private AppCompatButton btnBuildCode, btnBuildLogoCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rgParent = findViewById(R.id.rg_parent);
        btnScan = findViewById(R.id.btn_scan);
        tvCode = findViewById(R.id.tv_code);
        ivCode = findViewById(R.id.ivcode);
        btnBuildCode = findViewById(R.id.btn_buildcode);
        btnBuildLogoCode = findViewById(R.id.btn_buildlogocode);

        setListener();
    }

    private void setListener() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgParent.getCheckedRadioButtonId();
                switch (checkedRadioButtonId){
                    case R.id.rb_none:
                        startScan(ScanStyle.NONE);
                        break;
                    case R.id.rb_qq:
                        startScan(ScanStyle.QQ);
                        break;
                    case R.id.rb_wechat:
                        startScan(ScanStyle.WECHAT);
                        break;
                    default:
                        break;
                }
            }
        });
        //生成二维码
        btnBuildCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ScanCodeConfig.createQRCode("star");
                ivCode.setImageBitmap(bitmap);
            }
        });

        //生成带logo二维码
        btnBuildLogoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ScanCodeConfig.createQRcodeWithLogo("star", BitmapFactory.decodeResource(getResources(), R.mipmap.timg));
                ivCode.setImageBitmap(bitmap);
            }
        });
    }

    //开始扫描
    private void startScan(int style) {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }
                    @Override
                    public void onNext(@NonNull Permission permission) {
                        if(permission.granted){
                            ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(false)
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(ScanCodeActivity.class);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //接收扫码结果
        if(resultCode == RESULT_OK && requestCode == ScanCodeConfig.QUESTCODE && data != null){
            Bundle extras = data.getExtras();
            if(extras != null){
                String code = extras.getString(ScanCodeConfig.CODE_KEY);
                tvCode.setText(String.format("%s%s", "结果： " , code));
            }
        }
    }
}
