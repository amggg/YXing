package com.yxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.yxing.utils.QrCodeUtil;

public class ScanCodeConfig {
    public static final int QUESTCODE = 0x001;
    public static final String CODE_KEY = "code";
    public static final String MODEL_KEY = "model";

    private  Activity mActivity;
    private ScanCodeModel model;

    public ScanCodeConfig (ScanCodeModel model){
        this.mActivity = model.mActivity;
        this.model = model;
    }

    public static ScanCodeModel create(Activity mActivity){
        return new ScanCodeModel(mActivity);
    }

    public void start(Class mClass){
        Intent intent = new Intent(mActivity, mClass);
        intent.putExtra(MODEL_KEY, model);
        mActivity.startActivityForResult(intent, QUESTCODE);
    }

    public static Bitmap createQRCode(String text){
        return QrCodeUtil.createQRCode(text);
    }

    public static Bitmap createQRCode(String text, int size) {
        return QrCodeUtil.createQRCode(text, size);
    }

    public static Bitmap createQRcodeWithLogo(String text, Bitmap logo){
        return QrCodeUtil.createQRcodeWithLogo(text, logo);
    }

    public static Bitmap createQRcodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY){
        return QrCodeUtil.createQRcodeWithLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY);
    }

    public static Bitmap createQRcodeWithStrokLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY, int storkWith, int storkColor){
        return QrCodeUtil.createQRcodeWithStrokLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY, storkWith, storkColor);
    }

    public static Bitmap createBarcode(String content, int widthPix, int heightPix, boolean isShowContent){
        return QrCodeUtil.createBarcode(content, widthPix, heightPix, isShowContent);
    }

    public static String scanningImage(Activity mActivity, Uri uri) {
        return QrCodeUtil.scanningImage(mActivity, uri);
    }
}
