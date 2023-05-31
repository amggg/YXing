package com.yxing

import android.util.Size

object Config {

    const val TAG = "YXing"

    /**
     * 默认二维码提示大小
     */
    val DEFAULT_CODE_HINT_SIZE = Size(120, 120)

    /**
     * 多二维码识别缓冲次数
     */
    const val MULTI_READER_MIN_COUNT = 8
}