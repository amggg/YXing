package com.yxing

import android.app.Activity
import android.graphics.ImageFormat
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.yxing.iface.OnScancodeListener
import com.yxing.utils.AudioUtil
import java.util.*

class ScanCodeAnalyzer(
    mActivity: Activity,
    private val scanCodeModel: ScanCodeModel,
    private val scanRect: Rect?,
    private val onScancodeListener: OnScancodeListener
) : ImageAnalysis.Analyzer {

    private val audioUtil: AudioUtil = AudioUtil(mActivity, scanCodeModel.audioId)
    private val reader: MultiFormatReader = initReader()
    private var mScanRect = Rect()

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

    /**
     * 图片分析
     */
    override fun analyze(image: ImageProxy) {
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
            if (scanRect.width() > height || scanRect.height() > width) {
                throw RuntimeException("Limit Size Must be within the picture width and height")
            }
            mScanRect.set(scanRect.left, scanRect.top, scanRect.right, scanRect.bottom)
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
            val result = reader.decode(bitmap)
            if (scanCodeModel.isPlayAudio) audioUtil.playBeepSoundAndVibrate()
            onScancodeListener.onBackCode(result)
        } catch (e: Exception) {
            image.close()
        } finally {
            image.close()
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