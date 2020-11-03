package com.yxing.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author apple
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef()
public @interface ScanStyle {
    int QQ = 0X001;
    int WECHAT = 0X002;
}
