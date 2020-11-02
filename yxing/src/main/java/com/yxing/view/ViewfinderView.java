/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yxing.view;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.example.yxing.R;
import com.google.zxing.ResultPoint;
import com.yxing.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int POINT_SIZE = 6;

    private  Paint paint;
    // 取景框外的背景颜色
    private  int maskColor;
    // 特征点的颜色
    private  int resultPointColor;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线
    Bitmap scanLight;
    Rect frame;
    Rect previewFrame;

    private ValueAnimator valueAnimator;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = ContextCompat.getColor(getContext(), R.color.viewfinder_mask);
        resultPointColor = ContextCompat.getColor(getContext(), R.color.possible_result_points);
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
        scanLight = BitmapFactory.decodeResource(resources,
                R.drawable.scan_line);

        frame = new Rect();
        previewFrame = new Rect();
    }


    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        // frame为取景框
        frame.set(0, 0 ,getWidth(), getHeight());
        if (valueAnimator == null) {
            startScanAnim(frame.top, frame.bottom, frame);
        }
        if (frame == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        paint.setColor(maskColor);
//        drawFrameBounds(canvas, frame);
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + 10);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
        float scaleX = frame.width() / (float) previewFrame.width();
        float scaleY = frame.height() / (float) previewFrame.height();
        // 绘制扫描线周围的特征点
        List<ResultPoint> currentPossible = possibleResultPoints;
        List<ResultPoint> currentLast = lastPossibleResultPoints;
        int frameLeft = frame.left;
        int frameTop = frame.top;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new ArrayList<ResultPoint>(5);
            lastPossibleResultPoints = currentPossible;
            paint.setAlpha(CURRENT_POINT_OPACITY);
            paint.setColor(resultPointColor);
            synchronized (currentPossible) {
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft
                                    + (int) (point.getX() * scaleX), frameTop
                                    + (int) (point.getY() * scaleY), POINT_SIZE,
                            paint);
                }
            }
        }
        if (currentLast != null) {
            paint.setAlpha(CURRENT_POINT_OPACITY / 2);
            paint.setColor(resultPointColor);
            synchronized (currentLast) {
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft
                            + (int) (point.getX() * scaleX), frameTop
                            + (int) (point.getY() * scaleY), radius, paint);
                }
            }
        }
        postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE,
                frame.top - POINT_SIZE, frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE);
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        /*canvas.drawRect(frame, paint);*/
        paint.setStyle(Paint.Style.FILL);

        int corWidth = SizeUtils.dp2px(getContext(), 5);
        int corLength = SizeUtils.dp2px(getContext(), 15);

        // 左上角
        canvas.drawRect(frame.left - corWidth, frame.top, frame.left, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left - corWidth, frame.top - corWidth, frame.left
                + corLength, frame.top, paint);
        // 右上角
        canvas.drawRect(frame.right, frame.top, frame.right + corWidth,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top - corWidth,
                frame.right + corWidth, frame.top, paint);
        // 左下角
        canvas.drawRect(frame.left - corWidth, frame.bottom - corLength,
                frame.left, frame.bottom, paint);
        canvas.drawRect(frame.left - corWidth, frame.bottom, frame.left
                + corLength, frame.bottom + corWidth, paint);
        // 右下角
        canvas.drawRect(frame.right, frame.bottom - corLength, frame.right
                + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom, frame.right
                + corWidth, frame.bottom + corWidth, paint);
    }

    private void startScanAnim(int top, int bottom, Rect frame) {
        valueAnimator = ValueAnimator.ofInt(top, bottom);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scanLineTop = (int) animation.getAnimatedValue();
                if (scanLineTop > frame.bottom - 10) {
                    scanLineTop = frame.top;
                }
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
