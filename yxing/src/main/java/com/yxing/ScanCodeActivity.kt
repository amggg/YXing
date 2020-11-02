package com.yxing

import android.app.Activity
import android.content.Intent
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.widget.RelativeLayout
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.yxing.R
import com.yxing.iface.OnScancodeListenner
import com.yxing.view.ScanWechatView
import com.yxing.view.base.BaseScanView
import kotlinx.android.synthetic.main.activity_scancode.*
import java.io.File
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanCodeActivity : BaseScanActivity() {

    private var lensFacing : Int = CameraSelector.LENS_FACING_BACK
    private var camera : Camera? = null
    private var preview : Preview? = null
    private var imageAnalyzer : ImageAnalysis? = null
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var baseScanView : BaseScanView

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)
    }

    override fun getLayoutId() : Int = R.layout.activity_scancode

    override fun initData() {
        addScanView()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        // surface准备监听
        pvCamera.post {
            //设置需要实现的用例（预览，拍照，图片数据解析等等）
            bindCameraUseCases()
        }
    }

    private fun addScanView() {
        baseScanView = ScanWechatView(this)
        val lp : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        baseScanView.layoutParams = lp
        rlParent.addView(baseScanView)
    }

    private fun bindCameraUseCases() {

        // 获取用于设置全屏分辨率相机的屏幕值
        val metrics = DisplayMetrics().also { pvCamera.display.getRealMetrics(it) }

        //获取使用的屏幕比例分辨率属性
        val screenAspectRatio = aspectRatio(metrics.widthPixels / 2, metrics.heightPixels / 2)

        val width = pvCamera.measuredWidth
        val height = if (screenAspectRatio == AspectRatio.RATIO_16_9) {
            (width * RATIO_16_9_VALUE).toInt()
        } else {
            (width * RATIO_4_3_VALUE).toInt()
        }
        val size = Size(width, height)

        //获取旋转角度
        val rotation = pvCamera.display.rotation

        //生命周期绑定
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()//设置所选相机
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            // 预览用例
            preview = Preview.Builder()
                .setTargetResolution(size)
                .setTargetRotation(rotation)
                .build()

            // 图像分析用例
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(size)
                .setTargetRotation(rotation)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor, ScanCodeAnalyzer(this@ScanCodeActivity, object : OnScancodeListenner{
                        override fun onBackCode(code: String) {
                            val intent = Intent()
                            intent.putExtra(ScanCodeConfig.CODE_KEY, code)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }))
                }

            // 必须在重新绑定用例之前取消之前绑定
            cameraProvider.unbindAll()
            try {
                //获取相机实例
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

                //设置预览的view
                preview?.setSurfaceProvider(pvCamera.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 根据传入的值获取相机应该设置的分辨率比例
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        baseScanView.cancelAnim()
    }
}