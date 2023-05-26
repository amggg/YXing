package com.yxing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class BaseScanActivity : AppCompatActivity() {

    abstract fun getLayoutId(): Int

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initData()
        initImmersionBar()
    }

    private fun initImmersionBar() {
        ImmersionBar.with(this).init()
    }
}