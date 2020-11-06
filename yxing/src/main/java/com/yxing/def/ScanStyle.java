package com.yxing.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author apple
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ScanStyle.NONE, ScanStyle.QQ, ScanStyle.WECHAT, ScanStyle.CUSTOMIZE})
public @interface ScanStyle {
    int NONE = -1;
    int QQ = 1001;
    int WECHAT = 1002;
    int CUSTOMIZE = 1003;
}
