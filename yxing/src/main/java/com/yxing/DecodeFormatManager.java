package com.yxing;

import com.google.zxing.BarcodeFormat;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author am
 */
public final class DecodeFormatManager {

    public static final Set<BarcodeFormat> ONE_CODE;
    public static final Set<BarcodeFormat> TWO_CODE;

    static {
        ONE_CODE = getOneCodeFormats();
        TWO_CODE = getTwoCodeFormats();
    }

    /**
     * 一维码类型
     */
    private static Set<BarcodeFormat> getOneCodeFormats() {
        return EnumSet.of(BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.ITF,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION);
    }


    /**
     * 二维码类型
     */
    private static Set<BarcodeFormat> getTwoCodeFormats() {
        return EnumSet.of(BarcodeFormat.QR_CODE,
                BarcodeFormat.MAXICODE,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.PDF_417,
                BarcodeFormat.AZTEC);
    }
}
