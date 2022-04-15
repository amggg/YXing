package com.yxing

import android.app.Activity
import android.graphics.ImageFormat
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.yxing.iface.OnScancodeListener
import com.yxing.utils.AudioUtil
import java.nio.ByteBuffer
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
            Log.e("ScanCodeAnalyzer", "expect YUV_420_888, now = ${image.format}")
            image.close()
            return
        }

        //将buffer数据写入数组
        val data = image.planes[0].buffer.toByteArray()
        //图片宽高
        val width = image.width
        val height = image.height

        mScanRect.set(0, 0, width, height)

        //限制区域
        if (scanCodeModel.isLimitRect && scanRect != null) {
            mScanRect.set(scanRect.top, height - (scanRect.left + scanRect.width()), scanRect.bottom, height - scanRect.left)
        }

        val source = PlanarYUVLuminanceSource(
            data,
            width,
            height,
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
            onScancodeListener.onBackCode(result.text)
        } catch (e: Exception) {
            image.close()
        } finally {
            image.close()
        }
    }
}