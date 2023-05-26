package com.yxing.utils

import android.graphics.*

object ImageFormatUtil {

    fun yuvToBitmap(yuvData: ByteArray, width: Int, height: Int): Bitmap {
        val rgbData = convertYuvToRgb(yuvData, width, height)
        return createBitmapFromRgb(rgbData, width, height)
    }

    private fun convertYuvToRgb(yuvData: ByteArray, width: Int, height: Int): IntArray {
        val imageSize = width * height
        val uvSize = imageSize / 4

        val rgbData = IntArray(imageSize)

        var p = 0
        var i = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val yValue = yuvData[y * width + x].toInt() and 0xFF
                val uvIndex = imageSize + (y / 2) * (width / 2) + (x / 2) * 2

                val uValue = yuvData[uvIndex].toInt() and 0xFF
                val vValue = yuvData[uvIndex + 1].toInt() and 0xFF

                val r = (yValue + 1.402 * (vValue - 128)).toInt()
                val g = (yValue - 0.344136 * (uValue - 128) - 0.714136 * (vValue - 128)).toInt()
                val b = (yValue + 1.772 * (uValue - 128)).toInt()

                rgbData[p++] = Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
                i += 1
            }
        }

        return rgbData
    }

    private fun createBitmapFromRgb(rgbData: IntArray, width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(rgbData, width, height, Bitmap.Config.ARGB_8888)
    }
}