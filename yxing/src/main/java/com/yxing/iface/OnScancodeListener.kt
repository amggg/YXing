package com.yxing.iface

/**
 * 识别成功回调
 */
interface OnScancodeListener {
    /**
     * 扫码内容回调
     */
    fun onBackCode(code: String)
}