package com.yxing;

import android.app.Activity;
import android.content.Intent;

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
}
