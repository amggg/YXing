package com.yxing;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;

import com.yxing.bean.ScanRect;

/**
 * @author apple
 */
public class ScanCodeModel implements Parcelable {

    protected Activity mActivity;
    protected Fragment mFragment;
    private int style;
    private int scanMode;
    private boolean isPlayAudio;
    private int audioId;
    private boolean showFrame;
    private boolean isLimitRect;
    private ScanRect scanRect;
    private int scanSize;
    private int offsetX;
    private int offsetY;
    private boolean usePx;
    private long scanDuration;
    private int frameColor;
    private boolean showShadow;
    private int shaowColor;
    private int scanBitmapId;
    private int frameWith;
    private int frameLenth;
    private int frameRaduis;

    public ScanCodeModel(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public ScanCodeModel(Activity mActivity, Fragment mFragment) {
        this.mActivity = mActivity;
        this.mFragment = mFragment;
    }

    protected ScanCodeModel(Parcel in) {
        style = in.readInt();
        scanMode = in.readInt();
        isPlayAudio = in.readByte() != 0;
        audioId = in.readInt();
        showFrame = in.readByte() != 0;
        isLimitRect = in.readByte() != 0;
        scanRect = in.readParcelable(ScanRect.class.getClassLoader());
        scanSize = in.readInt();
        offsetX = in.readInt();
        offsetY = in.readInt();
        usePx = in.readByte() != 0;
        scanDuration = in.readLong();
        frameColor = in.readInt();
        showShadow = in.readByte() != 0;
        shaowColor = in.readInt();
        scanBitmapId = in.readInt();
        frameWith = in.readInt();
        frameLenth = in.readInt();
        frameRaduis = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(style);
        dest.writeInt(scanMode);
        dest.writeByte((byte) (isPlayAudio ? 1 : 0));
        dest.writeInt(audioId);
        dest.writeByte((byte) (showFrame ? 1 : 0));
        dest.writeByte((byte) (isLimitRect ? 1 : 0));
        dest.writeParcelable(scanRect, flags);
        dest.writeInt(scanSize);
        dest.writeInt(offsetX);
        dest.writeInt(offsetY);
        dest.writeByte((byte) (usePx ? 1 : 0));
        dest.writeLong(scanDuration);
        dest.writeInt(frameColor);
        dest.writeByte((byte) (showShadow ? 1 : 0));
        dest.writeInt(shaowColor);
        dest.writeInt(scanBitmapId);
        dest.writeInt(frameWith);
        dest.writeInt(frameLenth);
        dest.writeInt(frameRaduis);
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

    public ScanCodeModel setScanDuration(long scanDuration) {
        this.scanDuration = scanDuration;
        return this;
    }

    public long getScanDuration() {
        return scanDuration;
    }

    public ScanCodeModel setScanMode(int scanMode) {
        this.scanMode = scanMode;
        return this;
    }

    public int getScanMode() {
        return scanMode;
    }

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

    public int getAudioId() {
        return audioId;
    }

    public ScanCodeModel setAudioId(int audioId) {
        this.audioId = audioId;
        return this;
    }

    public boolean isShowFrame() {
        return showFrame;
    }

    public ScanCodeModel setShowFrame(boolean showFrame) {
        this.showFrame = showFrame;
        return this;
    }

    public boolean isLimitRect() {
        return isLimitRect;
    }

    public ScanCodeModel setLimitRect(boolean limitRect) {
        this.isLimitRect = limitRect;
        return this;
    }

    public ScanRect getScanRect() {
        return scanRect;
    }

    @Deprecated
    public ScanCodeModel setScanRect(ScanRect scanRect) {
        this.scanRect = scanRect;
        return this;
    }

    public int getScanSize() {
        return scanSize;
    }

    public ScanCodeModel setScanSize(int scanSize, int offsetX, int offsetY) {
        this.scanSize = scanSize;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public ScanCodeModel setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public ScanCodeModel setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public ScanCodeModel setScanRect(ScanRect scanRect, boolean isUsePx) {
        this.scanRect = scanRect;
        this.usePx = isUsePx;
        return this;
    }

    public boolean isUsePx() {
        return usePx;
    }

    public void setUsePx(boolean usePx) {
        this.usePx = usePx;
    }

    public int getFrameColor() {
        return frameColor;
    }

    public ScanCodeModel setFrameColor(int frameColor) {
        this.frameColor = frameColor;
        return this;
    }

    public boolean isShowShadow() {
        return showShadow;
    }

    public ScanCodeModel setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
        return this;
    }

    public int getShaowColor() {
        return shaowColor;
    }

    public ScanCodeModel setShaowColor(int shaowColor) {
        this.shaowColor = shaowColor;
        return this;
    }


    public int getScanBitmapId() {
        return scanBitmapId;
    }

    public ScanCodeModel setScanBitmapId(int scanBitmapId) {
        this.scanBitmapId = scanBitmapId;
        return this;
    }

    public int getFrameWith() {
        return frameWith;
    }

    public ScanCodeModel setFrameWith(int frameWith) {
        this.frameWith = frameWith;
        return this;
    }

    public int getFrameLenth() {
        return frameLenth;
    }

    public ScanCodeModel setFrameLenth(int frameLenth) {
        this.frameLenth = frameLenth;
        return this;
    }

    public int getFrameRaduis() {
        return frameRaduis;
    }

    public ScanCodeModel setFrameRaduis(int frameRaduis) {
        this.frameRaduis = frameRaduis;
        return this;
    }

    public ScanCodeConfig buidler() {
        return new ScanCodeConfig(this);
    }
}
