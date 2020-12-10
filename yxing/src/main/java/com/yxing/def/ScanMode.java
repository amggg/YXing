package com.yxing.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author apple
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ScanMode.RESTART, ScanMode.REVERSE})
public @interface ScanMode {
    int RESTART = 1;
    int REVERSE = 2;
}
