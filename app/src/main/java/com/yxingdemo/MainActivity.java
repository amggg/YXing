package com.yxingdemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.yxing.ScanCodeActivity;
import com.yxing.ScanCodeConfig;
import com.yxing.def.ScanMode;
import com.yxing.def.ScanStyle;
import com.yxing.utils.SizeUtils;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author apple
 */
public class MainActivity extends AppCompatActivity {

    private RadioGroup rgParent, rgScanType, rgCodeColor;
    private AppCompatButton btnScan, btnTwoScan, btnScanMyStyle;
    private AppCompatTextView tvCode;
    private AppCompatImageView ivCode;
    private AppCompatButton btnBuildCode,
            btnBuildLogoCode,
            btnBuildStorkLogoCode,
            btnBuildBarCode,
            btnScanAlbum;

    private boolean isMultiple = false;

    private final ActivityResultLauncher<String> albumLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result == null) {
                return;
            }
            //接收图片识别结果
            String code = ScanCodeConfig.scanningImage(MainActivity.this, result);
            tvCode.setText(String.format("识别结果： %s", code));
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rgParent = findViewById(R.id.rg_parent);
        rgScanType = findViewById(R.id.rg_scan_type);
        rgCodeColor = findViewById(R.id.rg_codecolor);
        btnScan = findViewById(R.id.btn_scan);
        btnTwoScan = findViewById(R.id.btn_scantwo);
        btnScanMyStyle = findViewById(R.id.btn_scanmystyle);
        tvCode = findViewById(R.id.tv_code);
        ivCode = findViewById(R.id.ivcode);
        btnBuildCode = findViewById(R.id.btn_buildcode);
        btnBuildLogoCode = findViewById(R.id.btn_buildlogocode);
        btnBuildStorkLogoCode = findViewById(R.id.btn_buildstorklogocode);
        btnBuildBarCode = findViewById(R.id.btn_buildbarcode);
        btnScanAlbum = findViewById(R.id.btn_scan_album);

        setListener();
    }

    private void setListener() {
        //设置识别类型 单码识别 或 多码识别
        rgScanType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one_code:
                        isMultiple = false;
                        break;
                    case R.id.rb_multiple_code:
                        isMultiple = true;
                        break;
                    default:
                        break;
                }
            }
        });
        //预定义界面
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgParent.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_none:
                        startScan(ScanStyle.NONE, ScanCodeActivity.class);
                        break;
                    case R.id.rb_qq:
                        startScan(ScanStyle.QQ, ScanCodeActivity.class);
                        break;
                    case R.id.rb_wechat:
                        startScan(ScanStyle.WECHAT, ScanCodeActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });

        // ScanStyle.CUSTOMIZE 配置界面
        btnTwoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCustomization();
            }
        });

        //自定义界面
        btnScanMyStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgParent.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_none:
                        startScan(ScanStyle.NONE, MyScanActivity.class);
                        break;
                    case R.id.rb_qq:
                        startScan(ScanStyle.QQ, MyScanActivity.class);
                        break;
                    case R.id.rb_wechat:
                        startScan(ScanStyle.WECHAT, MyScanActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });

        //识别相册内二维码
        btnScanAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAlbum();
            }
        });

        //生成二维码
        btnBuildCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgCodeColor.getCheckedRadioButtonId();
                Bitmap bitmap = null;
                switch (checkedRadioButtonId) {
                    case R.id.rb_green:
                        bitmap = ScanCodeConfig.createQRCode("star", 500, ContextCompat.getColor(MainActivity.this, R.color.green), Color.WHITE);
                        break;
                    case R.id.rb_red:
                        bitmap = ScanCodeConfig.createQRCode("star", 500, Color.RED, Color.WHITE);
                        break;
                    case R.id.rb_black:
                    default:
                        bitmap = ScanCodeConfig.createQRCode("star", 500, Color.BLACK, Color.WHITE);
                        break;
                }
                ivCode.setImageBitmap(bitmap);
            }
        });

        //生成带logo二维码
        btnBuildLogoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgCodeColor.getCheckedRadioButtonId();
                Bitmap bitmap = null;
                switch (checkedRadioButtonId) {
                    case R.id.rb_green:
                        bitmap = ScanCodeConfig.createQRCodeWithLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), ContextCompat.getColor(MainActivity.this, R.color.green), Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10));
                        break;
                    case R.id.rb_red:
                        bitmap = ScanCodeConfig.createQRCodeWithLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), Color.RED, Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10));
                        break;
                    case R.id.rb_black:
                    default:
                        bitmap = ScanCodeConfig.createQRCodeWithLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), Color.BLACK, Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10));
                        break;
                }
                ivCode.setImageBitmap(bitmap);
            }
        });

        //生成带描边logo二维码
        btnBuildStorkLogoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgCodeColor.getCheckedRadioButtonId();
                Bitmap bitmap = null;
                switch (checkedRadioButtonId) {
                    case R.id.rb_green:
                        bitmap = ScanCodeConfig.createQRCodeWithStrokeLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), ContextCompat.getColor(MainActivity.this, R.color.green), Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 2), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        break;
                    case R.id.rb_red:
                        bitmap = ScanCodeConfig.createQRCodeWithStrokeLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), Color.RED, Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 2), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        break;
                    case R.id.rb_black:
                    default:
                        bitmap = ScanCodeConfig.createQRCodeWithStrokeLogo("star", SizeUtils.dp2px(getApplicationContext(), 200), Color.BLACK, Color.WHITE, BitmapFactory.decodeResource(getResources(), R.mipmap.ttk), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 60), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 10), SizeUtils.dp2px(getApplicationContext(), 2), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        break;
                }
                ivCode.setImageBitmap(bitmap);
            }
        });

        //生成条形码
        btnBuildBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rgCodeColor.getCheckedRadioButtonId();
                Bitmap bitmap = null;
                switch (checkedRadioButtonId) {
                    case R.id.rb_green:
                        bitmap = ScanCodeConfig.createBarCode("23423423523", 500, 200, ContextCompat.getColor(MainActivity.this, R.color.green), Color.WHITE, ContextCompat.getColor(MainActivity.this, R.color.green), true);
                        break;
                    case R.id.rb_red:
                        bitmap = ScanCodeConfig.createBarCode("23423423523", 500, 200, Color.RED, Color.WHITE, Color.RED, true);
                        break;
                    case R.id.rb_black:
                    default:
                        bitmap = ScanCodeConfig.createBarCode("23423423523", 500, 200, Color.BLACK, Color.WHITE, Color.BLACK, true);
                        break;
                }
                ivCode.setImageBitmap(bitmap);
            }
        });
    }

    private void toAlbum() {
        albumLauncher.launch("image/*");
    }


    private void toCustomization() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Permission permission) {
                        if (permission.granted) {
                            ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式  ScanStyle.CUSTOMIZE ： 自定义样式
                                    .setStyle(ScanStyle.CUSTOMIZE)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(true)
                                    //设置音效音频
                                    .setAudioId(R.raw.beep)
                                    ////////////////////////////////////////////
                                    //以下配置 在style为 ScanStyle.CUSTOMIZE 时生效
                                    //设置扫码框位置  left ： 边框左边位置   top ： 边框上边位置   right ： 边框右边位置   bottom ： 边框下边位置   单位/dp
//                                    .setScanRect(new ScanRect(50, 200, 300, 450), false)
                                    //是否限制识别区域为设定扫码框大小  true:限制  false：不限制   默认false：识别区域为整个屏幕
                                    .setLimitRect(true)
                                    //设置扫码框位置 scanSize : 扫码框大小   offsetX ： x轴偏移量    offsetY ：y轴偏移量   单位 /px
                                    .setScanSize(600, 0, 0)
                                    //是否显示边框上四个角标 true ： 显示  false ： 不显示
                                    .setShowFrame(true)
                                    //设置边框上四个角标颜色
                                    .setFrameColor(R.color.whilte)
                                    //设置边框上四个角标圆角  单位 /dp
                                    .setFrameRadius(2)
                                    //设置边框上四个角宽度 单位 /dp
                                    .setFrameWith(4)
                                    //设置边框上四个角长度 单位 /dp
                                    .setFrameLength(15)
                                    //设置是否显示边框外部阴影 true ： 显示  false ： 不显示
                                    .setShowShadow(true)
                                    //设置边框外部阴影颜色
                                    .setShadeColor(R.color.black_tran30)
                                    //设置扫码条运动方式   ScanMode.REVERSE : 往复运动   ScanMode.RESTART ：重复运动    默认ScanMode.RESTART
                                    .setScanMode(ScanMode.REVERSE)
                                    //设置扫码条扫一次时间  单位/ms  默认3000
                                    .setScanDuration(3000)
                                    //设置扫码条图片
                                    .setScanBitmapId(R.mipmap.scan_wechatline)
                                    //////////////////////////////////////////////
                                    //////////////////////////////////////////////
                                    //以下配置在 setIdentifyMultiple 为 true 时生效
                                    //设置是否开启识别多个二维码 true：开启 false：关闭   开启后识别到多个二维码会停留在扫码页 手动选择需要解析的二维码后返回结果
                                    .setIdentifyMultiple(isMultiple)
                                    //设置 二维码提示按钮的宽度 单位：px
                                    .setQrCodeHintDrawableWidth(120)
                                    //设置 二维码提示按钮的高度 单位：px
                                    .setQrCodeHintDrawableHeight(120)
                                    //设置 二维码提示按钮的Drawable资源
//                                    .setQrCodeHintDrawableResource(R.mipmap.in)
                                    //设置 二维码提示Drawable 是否开启缩放动画效果
                                    .setStartCodeHintAnimation(true)
                                    //设置 二维码选择页 背景透明度
                                    .setQrCodeHintAlpha(0.5f)
                                    //////////////////////////////////////////////
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(MyScanActivity.class);
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

    //开始扫描
    private void startScan(int style, Class mClass) {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Permission permission) {
                        if (permission.granted) {
                            ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(true)
                                    //////////////////////////////////////////////
                                    //以下配置在 setIdentifyMultiple 为 true 时生效
                                    //设置是否开启识别多个二维码 true：开启 false：关闭   开启后识别到多个二维码会停留在扫码页 手动选择需要解析的二维码后返回结果
                                    .setIdentifyMultiple(isMultiple)
                                    //设置 二维码提示按钮的宽度 单位：px
                                    .setQrCodeHintDrawableWidth(120)
                                    //设置 二维码提示按钮的高度 单位：px
                                    .setQrCodeHintDrawableHeight(120)
                                    //设置 二维码提示按钮的Drawable资源
//                                    .setQrCodeHintDrawableResource(R.mipmap.in)
                                    //设置 二维码提示Drawable 是否开启缩放动画效果
                                    .setStartCodeHintAnimation(true)
                                    //设置 二维码选择页 背景透明度
                                    .setQrCodeHintAlpha(0.5f)
                                    //////////////////////////////////////////////
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(mClass);
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
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case ScanCodeConfig.QUESTCODE:
                    //接收扫码结果
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        int codeType = extras.getInt(ScanCodeConfig.CODE_TYPE);
                        String code = extras.getString(ScanCodeConfig.CODE_KEY);
                        tvCode.setText(String.format(
                                "扫码结果：\n" +
                                        "码类型: %s  \n" +
                                        "码值  : %s", codeType == 0 ? "一维码" : "二维码", code));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
