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
    public static final int QUESTCODE = 0x010;
    public static final String CODE_KEY = "code";
    public static final String CODE_TYPE = "code_type";
    protected static final String MODEL_KEY = "model";

    private final Activity mActivity;
    private final Fragment mFragment;
    private final ScanCodeModel mModel;

    public ScanCodeConfig(ScanCodeModel mModel) {
        this.mActivity = mModel.mActivity;
        this.mFragment = mModel.mFragment;
        this.mModel = mModel;
    }

    public static ScanCodeModel create(Activity mActivity) {
        return new ScanCodeModel(mActivity);
    }

    public static ScanCodeModel create(Activity mActivity, Fragment mFragment) {
        return new ScanCodeModel(mActivity, mFragment);
    }

    public void start(Class<?> mClass) {
        if (mFragment != null) {
            Intent intent = new Intent(mActivity, mClass);
            intent.putExtra(MODEL_KEY, mModel);
            mFragment.startActivityForResult(intent, QUESTCODE);
        } else {
            Intent intent = new Intent(mActivity, mClass);
            intent.putExtra(MODEL_KEY, mModel);
            mActivity.startActivityForResult(intent, QUESTCODE);
        }
    }

    public static Bitmap createQRCode(String text) {
        return QrCodeUtil.createQrCode(text);
    }

    public static Bitmap createQRCode(String text, int size) {
        return QrCodeUtil.createQrCode(text, size);
    }

    public static Bitmap createQRCode(String text, int size, int codeColor, int backgroundColor) {
        return QrCodeUtil.createQrCode(text, size, codeColor, backgroundColor);
    }

    public static Bitmap createQRCodeWithLogo(String text, Bitmap logo) {
        return QrCodeUtil.createQrCodeWithLogo(text, logo);
    }

    public static Bitmap createQRCodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRadiusX, float logoRadiusY) {
        return QrCodeUtil.createQrCodeWithLogo(text, size, logo, logoWith, logoHigh, logoRadiusX, logoRadiusY);
    }

    public static Bitmap createQRCodeWithLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWith, int logoHigh, float logoRadiusX, float logoRadiusY) {
        return QrCodeUtil.createQrCodeWithLogo(text, size, codeColor, backgroundColor, logo, logoWith, logoHigh, logoRadiusX, logoRadiusY);
    }

    public static Bitmap createQRCodeWithStrokeLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRadiusX, float logoRadiusY, int strokeWith, int strokeColor) {
        return QrCodeUtil.createQrCodeWithStrokeLogo(text, size, logo, logoWith, logoHigh, logoRadiusX, logoRadiusY, strokeWith, strokeColor);
    }

    public static Bitmap createQRCodeWithStrokeLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWith, int logoHigh, float logoRadiusX, float logoRadiusY, int strokeWith, int strokeColor) {
        return QrCodeUtil.createQrCodeWithStrokeLogo(text, size, codeColor, backgroundColor, logo, logoWith, logoHigh, logoRadiusX, logoRadiusY, strokeWith, strokeColor);
    }

    public static Bitmap createBarCode(String content, int widthPix, int heightPix, boolean isShowContent) {
        return QrCodeUtil.createBarCode(content, widthPix, heightPix, isShowContent);
    }

    public static Bitmap createBarCode(String content, int widthPix, int heightPix, int codeColor, int backgroundColor, int textColor, boolean isShowContent) {
        return QrCodeUtil.createBarCode(content, widthPix, heightPix, codeColor, backgroundColor, textColor, isShowContent);
    }

    public static String scanningImage(Activity mActivity, Uri uri) {
        return QrCodeUtil.scanningImage(mActivity, uri);
    }

    public static String scanningImageByBitmap(Bitmap bitmap) {
        return QrCodeUtil.scanningImageByBitmap(bitmap);
    }
}
