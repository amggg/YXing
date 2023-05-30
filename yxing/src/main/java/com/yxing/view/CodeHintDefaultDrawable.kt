package com.yxing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.yxing.R
import com.yxing.utils.SizeUtils

/**
 * 默认二维码提示点
 */
class CodeHintDefaultDrawable(private val mContext: Context) : Drawable() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(mContext, R.color.white)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        drawWhiteCircle(canvas, width, height)
        drawGreenCircle(canvas, width, height)
        drawArrowhead(canvas, width, height)
    }

    private fun drawGreenCircle(canvas: Canvas, width: Int, height: Int) {
        mPaint.color = ContextCompat.getColor(mContext, R.color.green)
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(
            (width shr 1).toFloat(),
            (height shr 1).toFloat(),
            ((width shr 1).toFloat() * 0.8).toFloat(), mPaint
        )
    }

    private fun drawWhiteCircle(canvas: Canvas, width: Int, height: Int) {
        mPaint.color = ContextCompat.getColor(mContext, R.color.white)
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(
            (width shr 1).toFloat(),
            (height shr 1).toFloat(),
            (width shr 1).toFloat(), mPaint
        )
    }

    private fun drawArrowhead(canvas: Canvas, width: Int, height: Int) {
        mPaint.color = ContextCompat.getColor(mContext, R.color.white)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = SizeUtils.dp2px(mContext, 2f).toFloat()
        val percent3Width = width / 3
        val percent2Height = height / 2
        val path = Path()
        path.moveTo((percent3Width * 1.7).toFloat(), (percent2Height * 0.7).toFloat())
        path.lineTo((percent3Width * 2).toFloat(), percent2Height.toFloat())
        path.lineTo((percent3Width * 1.7).toFloat(), (percent2Height * 1.3).toFloat())
        path.moveTo(percent3Width.toFloat(), percent2Height.toFloat())
        path.lineTo((percent3Width * 2).toFloat(), percent2Height.toFloat())
        canvas.drawPath(path, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return mPaint.alpha
    }
}