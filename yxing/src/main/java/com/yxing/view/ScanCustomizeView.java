package com.yxing.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.yxing.R;
import com.yxing.ScanCodeModel;
import com.yxing.bean.ScanRect;
import com.yxing.utils.SizeUtils;
import com.yxing.view.base.BaseScanView;

public class ScanCustomizeView extends BaseScanView {
    //边框角默认宽度
    public static final int DEFALUTE_WITH = 4;
    //边框角默认长度
    public static final int DEFAULTE_LENGTH = 15;
    //默认扫描速度
    public static final long DEFAULTE_SPEED = 3000;

    private Paint paint;
    private Bitmap scanLine;
    private Rect scanRect;
    private Rect lineRect;

    //扫描线位置
    private int scanLineTop;

    private int bitmapHigh;

    private ScanCodeModel scanCodeModel;

    private ScanRect sRect;

    public ScanCustomizeView(Context context) {
        super(context);
        init();
    }

    public ScanCustomizeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScanCustomizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public Rect getScanRect() {
        return scanRect;
    }

    public void setScanCodeModel(ScanCodeModel scanCodeModel) {
        this.scanCodeModel = scanCodeModel;

        scanLine = BitmapFactory.decodeResource(getResources(),
                scanCodeModel.getScanBitmapId());

        bitmapHigh = scanLine == null ? 0 : scanLine.getHeight();
        sRect = scanCodeModel.getScanRect();

        postInvalidate();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        scanRect = new Rect();
        lineRect = new Rect();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (scanCodeModel.getScanSize() != 0) {
            scanRect.set((getWidth() >> 1) - (scanCodeModel.getScanSize() >> 1) + scanCodeModel.getOffsetX(), (getHeight() >> 1) - (scanCodeModel.getScanSize() >> 1) + scanCodeModel.getOffsetY(), (getWidth() >> 1) + (scanCodeModel.getScanSize() >> 1) + scanCodeModel.getOffsetX(), (getHeight() >> 1) + (scanCodeModel.getScanSize() >> 1) + scanCodeModel.getOffsetY());
        } else if (sRect != null) {
            if (scanCodeModel.isUsePx()) {
                scanRect.set(sRect.getLeft(), sRect.getTop(), sRect.getRight(), sRect.getBottom());
            } else {
                scanRect.set(SizeUtils.dp2px(getContext(), sRect.getLeft()), SizeUtils.dp2px(getContext(), sRect.getTop()), SizeUtils.dp2px(getContext(), sRect.getRight()), SizeUtils.dp2px(getContext(), sRect.getBottom()));
            }
        }
        if (scanCodeModel.isShowFrame()) {
            drawFrameBounds(canvas, scanRect);
        }
        if (scanCodeModel.isShowShadow()) {
            drawShadow(canvas, scanRect);
        }
        if (scanLine != null) {
            initAnim();
            lineRect.set(scanRect.left, scanLineTop, scanRect.right, scanLineTop + bitmapHigh);
            canvas.drawBitmap(scanLine, null, lineRect, paint);
        }
    }

    /**
     * 绘制阴影
     *
     * @param canvas
     * @param frame
     */
    private void drawShadow(Canvas canvas, Rect frame) {
        paint.setColor(ContextCompat.getColor(getContext(), scanCodeModel.getShaowColor() == 0 ? R.color.black_tran30 : scanCodeModel.getShaowColor()));
        int frameWith = SizeUtils.dp2px(getContext(), scanCodeModel.getFrameWith() == 0 ? DEFALUTE_WITH : scanCodeModel.getFrameWith());
        canvas.drawRect(0, 0, getWidth(), frame.top - frameWith, paint);
        canvas.drawRect(0, frame.top - frameWith, frame.left - frameWith, frame.bottom + frameWith, paint);
        canvas.drawRect(frame.right + frameWith, frame.top - frameWith, getWidth(), frame.bottom + frameWith, paint);
        canvas.drawRect(0, frame.bottom + frameWith, getWidth(), getHeight(), paint);
    }


    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {
        paint.setColor(ContextCompat.getColor(getContext(), scanCodeModel.getFrameColor() == 0 ? R.color.qqscan : scanCodeModel.getFrameColor()));

        int corWidth = SizeUtils.dp2px(getContext(), scanCodeModel.getFrameWith() == 0 ? DEFALUTE_WITH : scanCodeModel.getFrameWith());
        int corLength = SizeUtils.dp2px(getContext(), scanCodeModel.getFrameLenth() == 0 ? DEFAULTE_LENGTH : scanCodeModel.getFrameLenth());
        int radius = SizeUtils.dp2px(getContext(), scanCodeModel.getFrameRaduis());

        // 左上角
        canvas.drawRoundRect(frame.left - corWidth, frame.top - corWidth, frame.left, frame.top
                + corLength, radius, radius, paint);
        canvas.drawRoundRect(frame.left - corWidth, frame.top - corWidth, frame.left
                + corLength, frame.top, radius, radius, paint);
        // 右上角
        canvas.drawRoundRect(frame.right, frame.top - corWidth, frame.right + corWidth,
                frame.top + corLength, radius, radius, paint);
        canvas.drawRoundRect(frame.right - corLength, frame.top - corWidth,
                frame.right + corWidth, frame.top, radius, radius, paint);
        // 左下角
        canvas.drawRoundRect(frame.left - corWidth, frame.bottom - corLength,
                frame.left, frame.bottom + corWidth, radius, radius, paint);
        canvas.drawRoundRect(frame.left - corWidth, frame.bottom, frame.left
                + corLength, frame.bottom + corWidth, radius, radius, paint);
        // 右下角
        canvas.drawRoundRect(frame.right, frame.bottom - corLength, frame.right
                + corWidth, frame.bottom + corWidth, radius, radius, paint);
        canvas.drawRoundRect(frame.right - corLength, frame.bottom, frame.right
                + corWidth, frame.bottom + corWidth, radius, radius, paint);
    }

    @Override
    public void initAnim() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(scanRect.top - bitmapHigh, scanRect.bottom - bitmapHigh);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(scanCodeModel.getScanMode() == 0 ? ValueAnimator.RESTART : scanCodeModel.getScanMode());
            valueAnimator.setDuration(scanCodeModel.getScanDuration() == 0 ? DEFAULTE_SPEED : scanCodeModel.getScanDuration());
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scanLineTop = (int) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void startAnim() {
        if (valueAnimator != null){
            valueAnimator.start();
        }
    }

    @Override
    public void pauseAnim() {
        if (valueAnimator != null){
            valueAnimator.pause();
        }
    }

    @Override
    public void cancelAnim() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
}
