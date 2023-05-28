package com.yxing

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import com.yxing.iface.OnScancodeListener
import com.yxing.utils.AudioUtil
import com.yxing.utils.ImageRotateUtil
import com.yxing.utils.ImageUtil
import com.yxing.utils.YuvToArrayUtil
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

    private var mLastMultiReaderCodeCount: Int = 0

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
    @SuppressLint("UnsafeOptInUsageError", "UnsafeExperimentalUsageError")
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
        val data = YuvToArrayUtil.yuvToArray(image.image!!)
        //图片宽高
        val width = image.width
        val height = image.height
        val rotateByteArray = ImageRotateUtil.instance.rotateYuvArrayImage(data, width, height, 90)

        mScanRect.set(0, 0, rotateByteArray.second, rotateByteArray.third)

        //限制区域
        if (scanCodeModel.isLimitRect && scanRect != null) {
            if (scanRect.width() <= height && scanRect.height() <= width) {
                mScanRect.set(scanRect.left, scanRect.top, scanRect.right, scanRect.bottom)
            }
        }

        val source = PlanarYUVLuminanceSource(
            rotateByteArray.first,
            rotateByteArray.second,
            rotateByteArray.third,
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
                if (results.size < 2) {
                    mLastMultiReaderCodeCount ++
                    if (mLastMultiReaderCodeCount <= Config.MULTI_READER_MIN_COUNT) {
                        return
                    }
                }
                mLastMultiReaderCodeCount = 0
                if (scanCodeModel.isPlayAudio) audioUtil.playSound()
                val snapshotBitmap = ImageUtil.nv21ToBitmap(rotateByteArray.first, rotateByteArray.second, rotateByteArray.third)
                onScancodeListener.onBackMultiResultCode(snapshotBitmap, results)
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
}