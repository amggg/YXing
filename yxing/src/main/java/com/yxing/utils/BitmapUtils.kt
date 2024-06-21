package com.yxing.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object BitmapUtils {
    /**
     * 保存 Bitmap 到指定文件路径
     *
     * @param bitmap   要保存的 Bitmap 对象
     * @param filePath 文件路径
     * @param format   图片格式，默认为 JPEG
     * @param quality  图片质量，默认为 100
     * @return true 表示保存成功，false 表示保存失败
     */
    fun saveBitmapToFile(
        bitmap: Bitmap,
        filePath: String,
        format: CompressFormat = CompressFormat.JPEG,
        quality: Int = 100
    ): Boolean {
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(filePath)
            bitmap.compress(format, quality, outputStream)
            outputStream.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 在应用的私有目录中保存 Bitmap 到指定文件名
     *
     * @param context  上下文对象
     * @param bitmap   要保存的 Bitmap 对象
     * @param fileName 文件名
     * @param format   图片格式，默认为 JPEG
     * @param quality  图片质量，默认为 100
     * @return 保存成功返回文件路径，保存失败返回 null
     */
    fun saveBitmapToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        format: CompressFormat = CompressFormat.JPEG,
        quality: Int = 100
    ): String? {
        val fileDir = context.filesDir
        val filePath = File(fileDir, fileName).absolutePath
        return if (saveBitmapToFile(bitmap, filePath, format, quality)) {
            filePath
        } else {
            null
        }
    }

    /**
     * 在外部存储中保存 Bitmap 到指定文件名
     *
     * @param bitmap   要保存的 Bitmap 对象
     * @param fileName 文件名
     * @param format   图片格式，默认为 JPEG
     * @param quality  图片质量，默认为 100
     * @return 保存成功返回文件路径，保存失败返回 null
     */
    fun saveBitmapToExternalStorage(
        bitmap: Bitmap,
        fileName: String,
        format: CompressFormat = CompressFormat.JPEG,
        quality: Int = 100
    ): String? {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val externalDir = Environment.getExternalStorageDirectory()
            val filePath = File(externalDir, fileName).absolutePath
            return if (saveBitmapToFile(bitmap, filePath, format, quality)) {
                filePath
            } else {
                null
            }
        }
        return null
    }
}
