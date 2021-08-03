package com.yxing

import android.app.Activity
import android.graphics.ImageFormat
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.yxing.iface.OnScancodeListenner
import com.yxing.utils.AudioUtil
import java.nio.ByteBuffer
import java.util.*

class ScanCodeAnalyzer(
    mActivity: Activity,
    val scanCodeModel: ScanCodeModel,
    val scanRect: Rect?,
    val onScancodeListenner: OnScancodeListenner
) : ImageAnalysis.Analyzer {

    private val audioUtil: AudioUtil = AudioUtil(mActivity, scanCodeModel.audioId)
    private val reader: MultiFormatReader = initReader()

    /**
     * 将buffer写入数组
     */
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    //图片分析
    override fun analyze(image: ImageProxy) {
        if (ImageFormat.YUV_420_888 != image.format) {
            Log.e("BarcodeAnalyzer", "expect YUV_420_888, now = ${image.format}")
            image.close()
            return
        }

        //将buffer数据写入数组
        val data = image.planes[0].buffer.toByteArray()

        //图片宽高
        val height = image.height
        val width = image.width

        //将图片旋转
        val rotationData = ByteArray(data.size)
        var j: Int
        var k: Int
        for (y in 0 until height) {
            for (x in 0 until width) {
                j = x * height + height - y - 1
                k = x + y * width
                rotationData[j] = data[k]
            }
        }
        var scanLeft = 0
        var scanTop = 0
        var scanWidth = height
        var scanHeight = width
        if (scanCodeModel.isLimitRect && scanRect != null) {
            scanLeft = scanRect.left
            scanTop = scanRect.top
            scanWidth = scanRect.height()
            scanHeight = scanRect.width()
        }
        val source = PlanarYUVLuminanceSource(
            rotationData,
            height,
            width,
            scanLeft,
            scanTop,
            scanWidth,
            scanHeight,
            false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = reader.decode(bitmap)
            if (scanCodeModel.isPlayAudio) audioUtil.playBeepSoundAndVibrate()
            onScancodeListenner.onBackCode(result.text)
        } catch (e: Exception) {
            image.close()
        } finally {
            image.close()
        }
    }

    private fun initReader(): MultiFormatReader {
        val formatReader = MultiFormatReader()
        val hints = Hashtable<DecodeHintType, Any>()
        val decodeFormats = Vector<BarcodeFormat>()

        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        formatReader.setHints(hints)
        return formatReader
    }
}