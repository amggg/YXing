package com.yxing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CodeMarkPointView extends View {

    private Paint paint;

    private int raduis = 20;
    private int pointColor = Color.GREEN;

    public CodeMarkPointView(Context context) {
        super(context);
        init();
    }

    public CodeMarkPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeMarkPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(pointColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, raduis, paint);
    }
}
