package com.yxing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import com.yxing.iface.OnScancodeListener
import com.yxing.utils.AudioUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ScanCodeAnalyzer(
    mActivity: Activity,
    private val scanCodeModel: ScanCodeModel,
    private val scanRect: Rect?,
    private val onScancodeListener: OnScancodeListener
) : ImageAnalysis.Analyzer {

    private val audioUtil: AudioUtil = AudioUtil(mActivity, scanCodeModel.audioId)
    private val reader: MultiFormatReader = initReader()
    private val mMultiResultReader: QRCodeMultiReader = QRCodeMultiReader()

    private var mScanRect: Rect = Rect()

    private var pauseAnalyzer = false

    private fun initReader(): MultiFormatReader {
        val formatReader = MultiFormatReader()
        val hints = Hashtable<DecodeHintType, Any>()
        val decodeFormats = Vector<BarcodeFormat>()
        decodeFormats.addAll(DecodeFormatManager.ONE_CODE)
        decodeFormats.addAll(DecodeFormatManager.TWO_CODE)
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        formatReader.setHints(hints)
        return formatReader
    }

    /**
     * 恢复多二维码识别
     */
    fun resumeMultiReader() {
        if (!scanCodeModel.isIdentifyMultiple) {
            return
        }
        this.pauseAnalyzer = false
    }


    /**
     * 图片分析
     */
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        if (pauseAnalyzer) {
            image.close()
            return
        }
        if (ImageFormat.YUV_420_888 != image.format) {
            image.close()
            throw Throwable("expect YUV_420_888, now = ${image.format}")
        }
        //将buffer数据写入数组
        val data = image.planes[0].buffer.toByteArray()
        //图片宽高
        val width = image.width
        val height = image.height
        val rotateByteArray = rotateImageByte(data, width, height)

        mScanRect.set(0, 0, height, width)

        //限制区域
        if (scanCodeModel.isLimitRect && scanRect != null) {
            if (scanRect.width() <= height && scanRect.height() <= width) {
                mScanRect.set(scanRect.left, scanRect.top, scanRect.right, scanRect.bottom)
            }
        }

        val source = PlanarYUVLuminanceSource(
            rotateByteArray,
            height,
            width,
            mScanRect.left,
            mScanRect.top,
            mScanRect.width(),
            mScanRect.height(),
            false
        )

        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            if (scanCodeModel.isIdentifyMultiple) {
                val results = mMultiResultReader.decodeMultiple(bitmap)
                if (results.isEmpty()) {
                    return
                }
                if (scanCodeModel.isPlayAudio) audioUtil.playSound()
                val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                onScancodeListener.onBackMultiResultCode(bitmap, results)
                pauseAnalyzer = true
            }else {
                val result = reader.decode(bitmap)
                if (scanCodeModel.isPlayAudio) audioUtil.playSound()
                onScancodeListener.onBackCode(result)
            }
        } catch (e: Exception) {
            image.close()
        } finally {
            image.close()
        }
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String) {
        val directory = context.cacheDir.absolutePath
        val file = File(directory, fileName)

        var fos: FileOutputStream? = null
        try {
            if (file.exists()) {
                file.delete()
            }
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            Log.e("am", "===> 保存成功")
        } catch (e: IOException) {
            Log.e("am", "===> 保存失败")
            e.printStackTrace()
        } finally {
            fos?.close()
        }
    }


    /**
     * 旋转图片
     */
    private fun rotateImageByte(oldByteData: ByteArray, width: Int, height: Int): ByteArray {
        val rotationData = ByteArray(oldByteData.size)
        var j: Int
        var k: Int
        for (y in 0 until height) {
            for (x in 0 until width) {
                j = x * height + height - y - 1
                k = x + y * width
                rotationData[j] = oldByteData[k]
            }
        }
        return rotationData
    }
}