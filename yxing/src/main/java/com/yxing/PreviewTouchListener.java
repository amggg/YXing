package com.yxing;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * @author am
 */
public class PreviewTouchListener implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private final ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private CustomTouchListener mCustomTouchListener;

    public PreviewTouchListener(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setCustomTouchListener(CustomTouchListener customTouchListener) {
        mCustomTouchListener = customTouchListener;
    }

    /**
     * 缩放监听
     */
    ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float delta = detector.getScaleFactor();
            if (mCustomTouchListener != null) {
                mCustomTouchListener.zoom(delta);
            }
            return true;
        }
    };


    /**
     * 单击监听
     */
    GestureDetector.OnDoubleTapListener onDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (mCustomTouchListener != null) {
                mCustomTouchListener.focus(motionEvent.getX(), motionEvent.getY());
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return true;
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event) | mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    public interface CustomTouchListener {
        /**
         * 放大
         */
        void zoom(float delta);

        /**
         * 对焦
         *
         * @param pointX 点击x坐标
         * @param pointY 点击y坐标
         */
        void focus(float pointX, float pointY);
    }

}
