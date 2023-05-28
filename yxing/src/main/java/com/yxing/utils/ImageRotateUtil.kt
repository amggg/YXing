package com.yxing.utils


class ImageRotateUtil {

    companion object {
        val instance: ImageRotateUtil = ImageRotateUtil()

        /**
         * 旋转角度
         */
        private const val ROTATE_DEGREE_90 = 90
        private const val ROTATE_DEGREE_180 = 180
        private const val ROTATE_DEGREE_270 = 270
    }

    /**
     * 旋转图片
     *return Triple :
     * first : 图片信息
     * second : 图片宽度
     * third ： 图片高度
     */
    fun rotateYuvArrayImage(
        imageArray: ByteArray,
        with: Int,
        high: Int,
        degree: Int
    ): Triple<ByteArray, Int, Int> {
        return when (degree) {
            ROTATE_DEGREE_90 -> {
                Triple(rotateYUV420Degree90(imageArray, with, high), high, with)
            }
            ROTATE_DEGREE_180 -> {
                Triple(rotateYUV420Degree180(imageArray, with, high), with, high)
            }
            ROTATE_DEGREE_270 -> {
                Triple(rotateYUV420Degree270(imageArray, with, high), high, with)
            }
            else -> Triple(imageArray, with, high)
        }
    }


    private fun rotateYUV420Degree90(
        data: ByteArray,
        imageWidth: Int,
        imageHeight: Int
    ): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i--
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth
                        + (x - 1)]
                i--
            }
            x -= 2
        }
        return yuv
    }

    private fun rotateYUV420Degree180(
        data: ByteArray,
        imageWidth: Int,
        imageHeight: Int
    ): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        var i = 0
        var count = 0
        i = imageWidth * imageHeight - 1
        while (i >= 0) {
            yuv[count] = data[i]
            count++
            i--
        }
        i = imageWidth * imageHeight * 3 / 2 - 1
        while (i >= imageWidth
            * imageHeight
        ) {
            yuv[count++] = data[i - 1]
            yuv[count++] = data[i]
            i -= 2
        }
        return yuv
    }


    private fun rotateYUV420Degree270(
        data: ByteArray, imageWidth: Int,
        imageHeight: Int
    ): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        val nWidth = 0
        val nHeight = 0
        var wh = 0
        var uvHeight = 0
        if (imageWidth != nWidth || imageHeight != nHeight) {
            wh = imageWidth * imageHeight
            uvHeight = imageHeight shr 1 // uvHeight = height / 2
        }
        // ??Y
        var k = 0
        for (i in 0 until imageWidth) {
            var nPos = 0
            for (j in 0 until imageHeight) {
                yuv[k] = data[nPos + i]
                k++
                nPos += imageWidth
            }
        }
        var i = 0
        while (i < imageWidth) {
            var nPos = wh
            for (j in 0 until uvHeight) {
                yuv[k] = data[nPos + i]
                yuv[k + 1] = data[nPos + i + 1]
                k += 2
                nPos += imageWidth
            }
            i += 2
        }
        return rotateYUV420Degree180(
            yuv,
            imageWidth,
            imageHeight
        )
    }
}