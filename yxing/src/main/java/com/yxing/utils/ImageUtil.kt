package com.yxing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Created on 2019/03/05.
 *
 */
object ImageUtil {

    /**
     * Convert Bitmap data to BGR bytes.
     *
     * @param bitmap bitmap
     * @return bgr
     */
    fun bitmapToBgr(bitmap: Bitmap): ByteArray {
        val bytes = bitmap.byteCount
        val buffer = ByteBuffer.allocate(bytes)
        bitmap.copyPixelsToBuffer(buffer)
        val temp = buffer.array()
        val pixels = ByteArray(bitmap.width * bitmap.height * 3)
        for (i in 0 until temp.size / 4) {
            pixels[i * 3] = temp[i * 4 + 2]
            pixels[i * 3 + 1] = temp[i * 4 + 1]
            pixels[i * 3 + 2] = temp[i * 4]
        }
        return pixels
    }

    /**
     * Convert nv21 data to Bitmap.
     *
     * @param data nv21 data
     * @param width image width
     * @param height image Height
     * @return Bitmap
     */
    fun nv21ToBitmap(data: ByteArray?, width: Int, height: Int): Bitmap {
        val yuvImage = YuvImage(data, ImageFormat.NV21, width, height, null)
        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(
                0,
                0,
                yuvImage.width,
                yuvImage.height
            ), 100, outputStream
        )
        val jpegData = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
    }


    /**
     * bitmap转nv21
     */
    fun bitmapToNv21(src: Bitmap, width: Int, height: Int): ByteArray {
        val argb = IntArray(width * height)
        src.getPixels(argb, 0, width, 0, 0, width, height)
        return argbToNv21(argb, width, height)
    }

    /**
     * ARGB数据转化为NV21数据
     *
     * @param argb   argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    private fun argbToNv21(argb: IntArray, width: Int, height: Int): ByteArray {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var index = 0
        val nv21 = ByteArray(width * height * 3 / 2)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val R = argb[index] and 0xFF0000 shr 16
                val G = argb[index] and 0x00FF00 shr 8
                val B = argb[index] and 0x0000FF
                val Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                val U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                val V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128
                nv21[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.size - 2) {
                    nv21[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    nv21[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }
                ++index
            }
        }
        return nv21
    }
}