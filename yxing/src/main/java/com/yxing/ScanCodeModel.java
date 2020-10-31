package com.yxing;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class ScanCodeModel implements Parcelable {
    protected Activity mActivity;
    private int style;
    private boolean isPlayAudio;

    public ScanCodeModel(Activity mActivity){
        this.mActivity = mActivity;
    }

    private ScanCodeModel(Parcel in) {
        style = in.readInt();
        isPlayAudio = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(style);
        dest.writeByte((byte) (isPlayAudio ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanCodeModel> CREATOR = new Creator<ScanCodeModel>() {
        @Override
        public ScanCodeModel createFromParcel(Parcel in) {
            return new ScanCodeModel(in);
        }

        @Override
        public ScanCodeModel[] newArray(int size) {
            return new ScanCodeModel[size];
        }
    };

    public int getStyle() {
        return style;
    }

    public ScanCodeModel setStyle(int style) {
        this.style = style;
        return this;
    }

    public boolean isPlayAudio() {
        return isPlayAudio;
    }

    public ScanCodeModel setPlayAudio(boolean playAudio) {
        isPlayAudio = playAudio;
        return this;
    }

    public ScanCodeConfig buidler(){
        return new ScanCodeConfig(this);
    }
}
