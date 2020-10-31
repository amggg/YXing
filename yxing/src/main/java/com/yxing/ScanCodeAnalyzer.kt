package com.yxing

import android.app.Activity
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.yxing.iface.OnScancodeListenner
import com.yxing.utils.AudioUtil
import java.nio.ByteBuffer
import java.util.*

class ScanCodeAnalyzer(mActivity: Activity, val onScancodeListenner: OnScancodeListenner) : ImageAnalysis.Analyzer  {

    private val audioUtil : AudioUtil = AudioUtil(mActivity)
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

    override fun analyze(image: ImageProxy) {//图片分析

        //如果不是yuv_420_888格式直接不处理
        if (ImageFormat.YUV_420_888 != image.format) {
            Log.e("BarcodeAnalyzer", "expect YUV_420_888, now = ${image.format}")
            image.close()
            return
        }

        //将buffer数据写入数组
        val data = image.planes[0].buffer.toByteArray()

        //获取图片宽高
        val height = image.height
        val width = image.width

        //将图片旋转，这是竖屏扫描的关键一步，因为默认输出图像是横的，我们需要将其旋转90度
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
        //zxing核心解码块，因为图片旋转了90度，所以宽高互换，最后一个参数是左右翻转
        val source = PlanarYUVLuminanceSource(rotationData, height, width, 0, 0, height, width, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = reader.decode(bitmap)
            audioUtil.playBeepSoundAndVibrate()
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

        //添加一维码解码格式
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
        //这个不知道干啥的，可以不加
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        //添加二维码解码格式
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)

        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        //设置解码的字符类型
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        //这边是焦点回调，就是找到那个条码的所在位置，这里我不处理
//        hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = mPointCallBack
        formatReader.setHints(hints)
        return formatReader
    }
}