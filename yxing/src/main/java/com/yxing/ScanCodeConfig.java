package com.yxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.yxing.utils.QrCodeUtil;

/**
 * @author am
 */
public class ScanCodeConfig {
    public static final int QUESTCODE = 0x001;
    public static final String CODE_KEY = "code";
    public static final String MODEL_KEY = "model";

    private final Activity mActivity;
    private final Fragment mFragment;
    private final ScanCodeModel model;

    public ScanCodeConfig(ScanCodeModel model) {
        this.mActivity = model.mActivity;
        this.mFragment = model.mFragment;
        this.model = model;
    }

    public static ScanCodeModel create(Activity mActivity) {
        return new ScanCodeModel(mActivity);
    }

    public static ScanCodeModel create(Activity mActivity, Fragment mFragment) {
        return new ScanCodeModel(mActivity, mFragment);
    }

    public void start(Class mClass) {
        if (mFragment != null) {
            Intent intent = new Intent(mActivity, mClass);
            intent.putExtra(MODEL_KEY, model);
            mFragment.startActivityForResult(intent, QUESTCODE);
        } else {
            Intent intent = new Intent(mActivity, mClass);
            intent.putExtra(MODEL_KEY, model);
            mActivity.startActivityForResult(intent, QUESTCODE);
        }
    }

    public static Bitmap createQRCode(String text) {
        return QrCodeUtil.createQRCode(text);
    }

    public static Bitmap createQRCode(String text, int size) {
        return QrCodeUtil.createQRCode(text, size);
    }

    public static Bitmap createQRCode(String text, int size, int codeColor, int backgroundColor) {
        return QrCodeUtil.createQRCode(text, size, codeColor, backgroundColor);
    }

    public static Bitmap createQRcodeWithLogo(String text, Bitmap logo) {
        return QrCodeUtil.createQRcodeWithLogo(text, logo);
    }

    public static Bitmap createQRcodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY) {
        return QrCodeUtil.createQRcodeWithLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY);
    }

    public static Bitmap createQRcodeWithLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY) {
        return QrCodeUtil.createQRcodeWithLogo(text, size, codeColor, backgroundColor, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY);
    }

    public static Bitmap createQRcodeWithStrokLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY, int storkWith, int storkColor) {
        return QrCodeUtil.createQRcodeWithStrokLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY, storkWith, storkColor);
    }

    public static Bitmap createQRcodeWithStrokLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY, int storkWith, int storkColor) {
        return QrCodeUtil.createQRcodeWithStrokLogo(text, size, codeColor, backgroundColor, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY, storkWith, storkColor);
    }

    public static Bitmap createBarcode(String content, int widthPix, int heightPix, boolean isShowContent) {
        return QrCodeUtil.createBarcode(content, widthPix, heightPix, isShowContent);
    }

    public static Bitmap createBarcode(String content, int widthPix, int heightPix, int codeColor, int backgroundColor, int textColor, boolean isShowContent) {
        return QrCodeUtil.createBarcode(content, widthPix, heightPix, codeColor, backgroundColor, textColor, isShowContent);
    }

    public static String scanningImage(Activity mActivity, Uri uri) {
        return QrCodeUtil.scanningImage(mActivity, uri);
    }

    public static String scanningImageByBitmap(Bitmap bitmap) {
        return QrCodeUtil.scanningImageByBitmap(bitmap);
    }
}
