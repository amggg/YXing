package com.yxing.iface

import com.google.zxing.Result

/**
 * 识别成功回调
 */
interface OnScancodeListener {
    /**
     * 扫码内容回调
     */
    fun onBackCode(result: Result)
}