package com.yxing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QrCodeUtil {

    public static final int DEFAULTE_SIZE = 500;

    /**
     * 生成二维码，默认大小为500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQRCode(String text) {
        return createQRCode(text, DEFAULTE_SIZE);
    }

    /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return bitmap
     */
    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 生成带描边logo 二维码
     * @param text  文字
     * @param size   二维码大小 1 ：1
     * @param logo   logo
     * @param logoWith logo宽
     * @param logoHigh  logo高
     * @param logoRaduisX  logo x圆角
     * @param logoRaduisY  logo y圆角
     * @param storkWith    描边宽度
     * @param storkColor   描边颜色
     * @return
     */
    public static Bitmap createQRcodeWithStrokLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY, int storkWith, int storkColor){
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            if(logo == null){
                return bitmap;
            }else{
                return addStorkLogo(bitmap, logo, logoWith, logoHigh, logoRaduisX,logoRaduisY, Math.min(storkWith, Math.min(logoWith, logoHigh)), storkColor);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 在二维码中间添加Logo图案(带描边)
     * @param src         原图
     * @param logo        logo
     * @param logoWith     添加logo的宽度
     * @param logoHigh     添加logo的高度
     * @param logoRaduisX  logo圆角
     * @param logoRaduisY  logo圆角
     * @param storkWith    描边宽度
     * @param storkColor   描边颜色
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap addStorkLogo(Bitmap src, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY, int storkWith, int storkColor) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int logoW = logo.getWidth();
        int logoH = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoW == 0 || logoH == 0) {
            return src;
        }
        float scaleW = logoWith / (float)logoW;
        float scaleH = logoHigh / (float)logoH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        matrix.postTranslate((srcWidth >> 1) - (logoWith >> 1), (srcHeight >> 1) - (logoHigh >> 1));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            paint.setColor(storkColor == 0 ? Color.WHITE : storkColor);
            canvas.drawRoundRect(new RectF((srcWidth >> 1) - (logoWith >> 1), (srcHeight >> 1) - (logoHigh >> 1), (srcWidth >> 1) + (logoWith >> 1), (srcHeight >> 1) + (logoHigh >> 1)), logoRaduisX, logoRaduisY, paint);
            paint.setShader(bitmapShader);
            canvas.drawRoundRect(new RectF(((srcWidth >> 1) - (logoWith >> 1)) + storkWith, ((srcHeight >> 1) - (logoHigh >> 1)) + storkWith, ((srcWidth >> 1) + (logoWith >> 1)) - storkWith, ((srcHeight >> 1) + (logoHigh >> 1)) - storkWith), logoRaduisX, logoRaduisY, paint);
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


    /**生成带logo 二维码
     * @param text  文字
     * @param logo   logo
     * @return
     */
    public static Bitmap createQRcodeWithLogo(String text, Bitmap logo){
        return createQRcodeWithLogo(text, DEFAULTE_SIZE, logo, DEFAULTE_SIZE / 5, DEFAULTE_SIZE / 5, 0 ,0);
    }


    /** 生成带logo 二维码
     * @param text  文字
     * @param size   二维码大小 1 ：1
     * @param logo   logo
     * @param logoWith logo宽
     * @param logoHigh  logo高
     * @param logoRaduisX  logo x圆角
     * @param logoRaduisY  logo y圆角
     * @return
     */
    public static Bitmap createQRcodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY){
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            if(logo == null){
                return bitmap;
            }else{
                return addLogo(bitmap, logo, logoWith, logoHigh, logoRaduisX,logoRaduisY);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在二维码中间添加Logo图案
     * @param src         原图
     * @param logo        logo
     * @param logoWith     添加logo的宽度
     * @param logoHigh     添加logo的高度
     * @param logoRaduisX  logo圆角
     * @param logoRaduisY  logo圆角
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap addLogo(Bitmap src, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int logoW = logo.getWidth();
        int logoH = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoW == 0 || logoH == 0) {
            return src;
        }

        float scaleW = logoWith / (float)logoW;
        float scaleH = logoHigh / (float)logoH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        matrix.postTranslate((srcWidth >> 1) - (logoWith >> 1), (srcHeight >> 1) - (logoHigh >> 1));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.drawRoundRect(new RectF((srcWidth >> 1) - (logoWith >> 1), (srcHeight >> 1) - (logoHigh >> 1), (srcWidth >> 1) + (logoWith >> 1), (srcHeight >> 1) + (logoHigh >> 1)), logoRaduisX, logoRaduisY, paint);
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * 解码uri二维码图片
     * @return
     */
    public static String scanningImage(Activity mActivity, Uri uri) {
        Bitmap scanBitmap = null;
        if (uri == null) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        //设置二维码内容的编码
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        scanBitmap = getBitmapByUri(mActivity, uri);
        RGBLuminanceSource source;
        if (scanBitmap != null) {
            source = new RGBLuminanceSource(scanBitmap);
        } else {
            return null;
        }
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints).getText();
        } catch (Exception e) {
            return null;
        }
    }


    private static Bitmap getBitmapByUri(Activity mActivity, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
