package com.yxing.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author windows
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ScanType.UN_KNOW, ScanType.CODE_ONE, ScanType.CODE_TWO})
public @interface ScanType {
    /**
     * 未知
     */
    int UN_KNOW = -1;
    /**
     * 一维码
     */
    int CODE_ONE = 0;
    /**
     * 二维码
     */
    int CODE_TWO = 1;
}
