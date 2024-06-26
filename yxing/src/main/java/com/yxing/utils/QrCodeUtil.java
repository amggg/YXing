package com.yxing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yxing.DecodeFormatManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admin
 */
public class QrCodeUtil {

    private static final int DEFAULT_SIZE = 500;
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    /**
     * 生成条形码
     *
     * @param content       要生成条形码包含的内容
     * @param widthPix      条形码的宽度
     * @param heightPix     条形码的高度
     * @param isShowContent 是否显示条形码包含的内容
     * @return 生成图片
     */
    public static Bitmap createBarCode(String content, int widthPix, int heightPix, boolean isShowContent) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (isContainChinese(content)) {
            return null;
        }
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // 图像数据转换，使用了矩阵转换 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (isShowContent) {
                bitmap = showContent(bitmap, content, Color.WHITE, Color.BLACK);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成自定义颜色条形码
     *
     * @param content         要生成条形码包含的内容
     * @param widthPix        条形码的宽度
     * @param heightPix       条形码的高度
     * @param codeColor       条码颜色
     * @param backgroundColor 背景颜色
     * @param textColor       内容颜色
     * @param isShowContent   是否显示条形码包含的内容
     * @return 生成图片
     */
    public static Bitmap createBarCode(String content, int widthPix, int heightPix, int codeColor, int backgroundColor, int textColor, boolean isShowContent) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (isContainChinese(content)) {
            return null;
        }
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // 图像数据转换，使用了矩阵转换 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = codeColor;
                    } else {
                        pixels[y * widthPix + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (isShowContent) {
                bitmap = showContent(bitmap, content, backgroundColor, textColor);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 显示条形的内容
     *
     * @param bcBitmap        已生成的条形码的位图
     * @param content         条形码包含的内容
     * @param backgroundColor 背景颜色
     * @param textColor       文字颜色
     * @return 生成图片
     */
    private static Bitmap showContent(Bitmap bcBitmap, String content, int backgroundColor, int textColor) {
        if (TextUtils.isEmpty(content) || null == bcBitmap) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        //测量字符串的宽度
        int textWidth = (int) paint.measureText(content);
        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制字符串矩形区域的高度
        int textHeight = (int) (fm.bottom - fm.top);
        // x 轴的缩放比率
        float scaleRateX = bcBitmap.getWidth() / textWidth;
        paint.setTextScaleX(scaleRateX);
        //绘制文本的基线
        int baseLine = bcBitmap.getHeight() + textHeight;
        //创建一个图层，然后在这个图层上绘制bCBitmap、content
        Bitmap bitmap = Bitmap.createBitmap(bcBitmap.getWidth(), (int) (bcBitmap.getHeight() + 1.5 * textHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(bcBitmap, 0, 0, null);
        canvas.drawText(content, (bcBitmap.getWidth() >> 1) - ((int) (textWidth * scaleRateX) >> 1), baseLine, paint);
        canvas.save();
        canvas.restore();
        return bitmap;
    }


    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    private static boolean isContainChinese(String str) {
        if ("".equals(str) || str == null) {
            throw new RuntimeException("content is empty!");
        }
        Matcher m = CHINESE_PATTERN.matcher(str);
        return m.find();
    }

    /**
     * 生成二维码，默认大小为500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQrCode(String text) {
        return createQrCode(text, DEFAULT_SIZE);
    }

    /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return 生成图片
     */
    public static Bitmap createQrCode(String text, int size) {
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

    /**
     * 生成自定义颜色二维码
     *
     * @param text            需要生成二维码的文字、网址等
     * @param size            需要生成二维码的大小（）
     * @param codeColor       二维码颜色
     * @param backgroundColor 背景颜色
     * @return 生成图片
     */
    public static Bitmap createQrCode(String text, int size, int codeColor, int backgroundColor) {
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
                        pixels[y * size + x] = codeColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
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


    /**
     * 生成带描边logo 二维码
     *
     * @param text        文字
     * @param size        二维码大小 1 ：1
     * @param logo        logo
     * @param logoWidth   logo宽
     * @param logoHeight  logo高
     * @param logoRadiusX logo x圆角
     * @param logoRadiusY logo y圆角
     * @param strokeWidth 描边宽度
     * @param strokeColor 描边颜色
     * @return 生成图片
     */
    public static Bitmap createQrCodeWithStrokeLogo(String text, int size, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY, int strokeWidth, int strokeColor) {
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
            if (logo == null) {
                return bitmap;
            } else {
                return addStrokeLogo(bitmap, logo, logoWidth, logoHeight, logoRadiusX, logoRadiusY, Math.min(strokeWidth, Math.min(logoWidth, logoHeight)), strokeColor);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成带描边logo 二维码  (自定义颜色)
     *
     * @param text            文字
     * @param size            二维码大小 1 ：1
     * @param codeColor       二维码颜色
     * @param backgroundColor 背景颜色
     * @param logo            logo
     * @param logoWidth       logo宽
     * @param logoHeight      logo高
     * @param logoRadiusX     logo x圆角
     * @param logoRadiusY     logo y圆角
     * @param strokeWidth     描边宽度
     * @param strokeColor     描边颜色
     * @return 生成图片
     */
    public static Bitmap createQrCodeWithStrokeLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY, int strokeWidth, int strokeColor) {
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
                        pixels[y * size + x] = codeColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            if (logo == null) {
                return bitmap;
            } else {
                return addStrokeLogo(bitmap, logo, logoWidth, logoHeight, logoRadiusX, logoRadiusY, Math.min(strokeWidth, Math.min(logoWidth, logoHeight)), strokeColor);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 在二维码中间添加Logo图案(带描边)
     *
     * @param src         原图
     * @param logo        logo
     * @param logoWidth   添加logo的宽度
     * @param logoHeight  添加logo的高度
     * @param logoRadiusX logo圆角
     * @param logoRadiusY logo圆角
     * @param strokeWidth 描边宽度
     * @param strokeColor 描边颜色
     * @return 生成图片
     */
    @SuppressLint("NewApi")
    private static Bitmap addStrokeLogo(Bitmap src, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY, int strokeWidth, int strokeColor) {
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
        float scaleW = logoWidth / (float) logoW;
        float scaleH = logoHeight / (float) logoH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        matrix.postTranslate((srcWidth >> 1) - (logoWidth >> 1), (srcHeight >> 1) - (logoHeight >> 1));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            paint.setColor(strokeColor == 0 ? Color.WHITE : strokeColor);
            canvas.drawRoundRect(new RectF((srcWidth >> 1) - (logoWidth >> 1) - strokeWidth, (srcHeight >> 1) - (logoHeight >> 1) - strokeWidth, (srcWidth >> 1) + (logoWidth >> 1) + strokeWidth, (srcHeight >> 1) + (logoHeight >> 1) + strokeWidth), logoRadiusX, logoRadiusY, paint);
            paint.setShader(bitmapShader);
            canvas.drawRoundRect(new RectF(((srcWidth >> 1) - (logoWidth >> 1)), ((srcHeight >> 1) - (logoHeight >> 1)), ((srcWidth >> 1) + (logoWidth >> 1)), ((srcHeight >> 1) + (logoHeight >> 1))), logoRadiusX, logoRadiusY, paint);
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


    /**
     * 生成带logo 二维码
     *
     * @param text 文字
     * @param logo logo
     * @return 生成图片
     */
    public static Bitmap createQrCodeWithLogo(String text, Bitmap logo) {
        return createQrCodeWithLogo(text, DEFAULT_SIZE, logo, DEFAULT_SIZE / 5, DEFAULT_SIZE / 5, 0, 0);
    }


    /**
     * 生成带logo 二维码
     *
     * @param text        文字
     * @param size        二维码大小 1 ：1
     * @param logo        logo
     * @param logoWidth   logo宽
     * @param logoHeight  logo高
     * @param logoRadiusX logo x圆角
     * @param logoRadiusY logo y圆角
     * @return 生成图片
     */
    public static Bitmap createQrCodeWithLogo(String text, int size, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY) {
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
            if (logo == null) {
                return bitmap;
            } else {
                return addLogo(bitmap, logo, logoWidth, logoHeight, logoRadiusX, logoRadiusY);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成带logo 二维码 (自定义颜色)
     *
     * @param text            文字
     * @param size            二维码大小 1 ：1
     * @param codeColor       二维码颜色
     * @param backgroundColor 背景颜色
     * @param logo            logo
     * @param logoWidth       logo宽
     * @param logoHeight      logo高
     * @param logoRadiusX     logo x圆角
     * @param logoRadiusY     logo y圆角
     * @return 生成图片
     */
    public static Bitmap createQrCodeWithLogo(String text, int size, int codeColor, int backgroundColor, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY) {
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
                        pixels[y * size + x] = codeColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            if (logo == null) {
                return bitmap;
            } else {
                return addLogo(bitmap, logo, logoWidth, logoHeight, logoRadiusX, logoRadiusY);
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在二维码中间添加Logo图案
     *
     * @param src         原图
     * @param logo        logo
     * @param logoWidth   添加logo的宽度
     * @param logoHeight  添加logo的高度
     * @param logoRadiusX logo圆角
     * @param logoRadiusY logo圆角
     * @return 生成图片
     */
    @SuppressLint("NewApi")
    private static Bitmap addLogo(Bitmap src, Bitmap logo, int logoWidth, int logoHeight, float logoRadiusX, float logoRadiusY) {
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

        float scaleW = logoWidth / (float) logoW;
        float scaleH = logoHeight / (float) logoH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        matrix.postTranslate((srcWidth >> 1) - (logoWidth >> 1), (srcHeight >> 1) - (logoHeight >> 1));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.drawRoundRect(new RectF((srcWidth >> 1) - (logoWidth >> 1), (srcHeight >> 1) - (logoHeight >> 1), (srcWidth >> 1) + (logoWidth >> 1), (srcHeight >> 1) + (logoHeight >> 1)), logoRadiusX, logoRadiusY, paint);
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * 解码uri二维码图片
     *
     * @return 解码内容
     */
    public static String scanningImage(Activity mActivity, Uri uri) {
        if (uri == null) {
            return null;
        }
        Bitmap srcBitmap = getBitmapByUri(mActivity, uri);
        return scanningImageByBitmap(srcBitmap);
    }


    /**
     * 解码bitmap二维码图片
     *
     * @return 解码内容
     */
    public static String scanningImageByBitmap(Bitmap srcBitmap) {
        if (srcBitmap == null) {
            return null;
        }
        MultiFormatReader formatReader = new MultiFormatReader();
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_CODE);
        decodeFormats.addAll(DecodeFormatManager.TWO_CODE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        formatReader.setHints(hints);
        Result result = null;
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        int[] pixels = new int[width * height];
        srcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            result = formatReader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        if (result != null) {
            return result.getText();
        }
        return null;
    }


    private static Bitmap getBitmapByUri(Activity mActivity, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
