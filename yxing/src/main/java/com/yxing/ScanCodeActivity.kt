package com.yxing

import android.animation.*
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.yxing.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.yxing.def.ScanStyle
import com.yxing.def.ScanType
import com.yxing.iface.OnScancodeListener
import com.yxing.view.CodeHintDefaultDrawable
import com.yxing.view.ScanCustomizeView
import com.yxing.view.ScanQqView
import com.yxing.view.ScanWechatView
import com.yxing.view.base.BaseScanView
import kotlinx.android.synthetic.main.activity_scancode.*
import java.lang.Math.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class ScanCodeActivity : BaseScanActivity(), OnScancodeListener {

    companion object {
        private const val TAG_CODE_HINT = "Yxing_CodeHintContainer"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    //设置所选相机
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var resolutionSize: Size
    private lateinit var camera: Camera
    private lateinit var preview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var cameraControl: CameraControl
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var cameraExecutor: ExecutorService
    private var baseScanView: BaseScanView? = null
    private var rlParentContent: RelativeLayout? = null
    private lateinit var scModel: ScanCodeModel

    private var rlCodeHintContainer: RelativeLayout? = null
    private var mScanCodeAnalyzer: ScanCodeAnalyzer? = null

    private var mAnimeSetList: MutableList<AnimatorSet> = mutableListOf()

    override fun getLayoutId(): Int = R.layout.activity_scancode

    override fun initData() {
        scModel = intent?.extras?.getParcelable(ScanCodeConfig.MODEL_KEY)!!
        initCodeHintContainer()
        addScanView(scModel.style)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        mScanCodeAnalyzer = ScanCodeAnalyzer(
            this,
            scModel,
            baseScanView?.scanRect,
            this
        )
        // surface准备监听
        pvCamera.post {
            //设置需要实现的用例（预览，拍照，图片数据解析等等）
            bindCameraUseCases()
        }
    }

    private fun initCodeHintContainer() {
        rlCodeHintContainer = RelativeLayout(this).apply {
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams = lp
            translationZ = 99f
            tag = TAG_CODE_HINT
        }
    }

    fun setFlashStatus(isOpenFlash: Boolean) {
        cameraControl.enableTorch(isOpenFlash)
    }

    private fun addScanView(style: Int?) {
        rlParentContent = findViewById(R.id.rlparent)
        when (style) {
            ScanStyle.QQ -> {
                baseScanView = ScanQqView(this)
            }
            ScanStyle.WECHAT -> {
                baseScanView = ScanWechatView(this)
            }
            ScanStyle.CUSTOMIZE -> {
                baseScanView = ScanCustomizeView(this).apply {
                    setScanCodeModel(scModel)
                }
            }
        }
        baseScanView?.let {
            val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            it.layoutParams = lp
            rlParentContent?.addView(it, 1)
        }
    }

    private fun bindCameraUseCases() {
        val width = pvCamera.measuredWidth
        val height = pvCamera.measuredHeight

        resolutionSize = Size(width, height)
        mScanCodeAnalyzer?.setResolutionSize(resolutionSize)

        //获取旋转角度
        val rotation = pvCamera.display.rotation

        //生命周期绑定
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            // 预览用例
            preview = getCameraPreView(rotation)

            // 图像分析用例
            imageAnalyzer = getCameraAnalyzer(rotation)

            // 必须在重新绑定用例之前取消之前绑定
            cameraProvider.unbindAll()

            try {
                //获取相机实例
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
                //设置预览的view
                preview.setSurfaceProvider(pvCamera.surfaceProvider)
                cameraControl = camera.cameraControl
                mCameraInfo = camera.cameraInfo

                bindTouchListener()
            } catch (exc: Exception) {
                Log.e(Config.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 获取相机预览用例
     */
    private fun getCameraPreView(rotation: Int): Preview {
        return Preview.Builder()
            .setTargetResolution(resolutionSize)
            .setTargetRotation(rotation)
            .build()
    }

    /**
     * 获取相机分析用例
     */
    private fun getCameraAnalyzer(rotation: Int): ImageAnalysis {
        val mImageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(resolutionSize)
            .setTargetRotation(rotation)
            .build()
        mScanCodeAnalyzer?.apply {
            mImageAnalysis.setAnalyzer(
                cameraExecutor,
                this
            )
        }
        return mImageAnalysis
    }

    /**
     * 触摸事件监听（放大、缩小、对焦）
     */
    private fun bindTouchListener() {
        val zoomState = mCameraInfo.zoomState
        val cameraXPreviewViewTouchListener =
            PreviewTouchListener(this)
        cameraXPreviewViewTouchListener.setCustomTouchListener(object :
            PreviewTouchListener.CustomTouchListener {
            override fun zoom(delta: Float) {
                zoomState.value?.let {
                    val currentZoomRatio = it.zoomRatio
                    cameraControl.setZoomRatio(currentZoomRatio * delta)
                }
            }

            override fun focus(pointX: Float, pointY: Float) {
                cameraFocus(pointX, pointY)
            }
        })
        pvCamera.setOnTouchListener(cameraXPreviewViewTouchListener)
    }

    /**
     * 对焦
     */
    private fun cameraFocus(pointX: Float, pointY: Float) {
        val factory = SurfaceOrientedMeteringPointFactory(
            resolutionSize.width.toFloat(),
            resolutionSize.height.toFloat()
        )
        val point = factory.createPoint(pointX, pointY)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
            // auto calling cancelFocusAndMetering in 4 seconds
            .setAutoCancelDuration(4, TimeUnit.SECONDS)
            .build()

        cameraControl.startFocusAndMetering(action)
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

    /**
     * 获取码类型
     */
    private fun getCodeType(barcodeFormat: BarcodeFormat): Int {
        if (DecodeFormatManager.TWO_CODE.contains(barcodeFormat)) {
            return ScanType.CODE_TWO
        }
        if (DecodeFormatManager.ONE_CODE.contains(barcodeFormat)) {
            return ScanType.CODE_ONE
        }
        return ScanType.UN_KNOW
    }

    private fun createCodeHintView(result: Result, realSize: Size): View {
        val resultPointOne = result.resultPoints[0]
        val resultPointTwo = result.resultPoints[1]
        val resultPointThree = result.resultPoints[2]

        var initOffsetX = 0
        var initOffsetY = 0
        if (scModel.isLimitRect && baseScanView?.scanRect != null) {
            initOffsetX = baseScanView?.scanRect?.left ?: 0
            initOffsetY = baseScanView?.scanRect?.top ?: 0
        }
        val scaleWidthFactor = resolutionSize.width / realSize.width.toFloat()
        val scaleHeightFactor = resolutionSize.height / realSize.height.toFloat()
        val offsetX = initOffsetX + (resultPointTwo.x + ((resultPointThree.x - resultPointTwo.x) / 4)) * scaleWidthFactor
        val offsetY = initOffsetY + (resultPointTwo.y + ((resultPointOne.y - resultPointTwo.y) / 4)) * scaleHeightFactor
        val ivCodeHint = AppCompatImageView(this)
        val hintDrawable = CodeHintDefaultDrawable(this)
        val width =
            if (scModel.qrCodeHintDrawableWidth > 0) scModel.qrCodeHintDrawableWidth else Config.DEFAULT_CODE_HINT_SIZE.width
        val height =
            if (scModel.qrCodeHintDrawableHeight > 0) scModel.qrCodeHintDrawableHeight else Config.DEFAULT_CODE_HINT_SIZE.height
        hintDrawable.setBounds(0, 0, width, height)
        val drawable = if (scModel.qrCodeHintDrawableResource > 0) ContextCompat.getDrawable(
            this,
            scModel.qrCodeHintDrawableResource
        ) else hintDrawable
        val lp = RelativeLayout.LayoutParams(width, height)
        lp.marginStart = offsetX.toInt()
        lp.topMargin = offsetY.toInt()
        ivCodeHint.layoutParams = lp
        ivCodeHint.setImageDrawable(drawable)
        ivCodeHint.setOnClickListener {
            callBackResult(result)
        }
        return ivCodeHint
    }

    private fun createCodeSnapshotView(bitmap: Bitmap): View {
        val ivCodeHint = AppCompatImageView(this)
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        ivCodeHint.layoutParams = lp
        ivCodeHint.scaleType = ImageView.ScaleType.FIT_XY
        ivCodeHint.setImageBitmap(bitmap)
        return ivCodeHint
    }

    private fun createSmegmaView(): View {
        val vSmegma = View(this)
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        vSmegma.layoutParams = lp
        vSmegma.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        vSmegma.alpha = scModel.qrCodeHintAlpha
        return vSmegma
    }

    private fun callBackResult(result: Result) {
        val intent = Intent()
        intent.putExtra(
            ScanCodeConfig.CODE_TYPE,
            getCodeType(result.barcodeFormat)
        )
        intent.putExtra(ScanCodeConfig.CODE_KEY, result.text)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 清除二维码提示
     */
    private fun clearCodeHintContainer(): Boolean {
        if (rlParentContent == null) {
            return false
        }
        for (i in 0 until rlParentContent!!.childCount) {
            val view = rlParentContent!!.getChildAt(i)
            if (TAG_CODE_HINT == view.tag) {
                (view as ViewGroup).removeAllViews()
                rlParentContent!!.removeView(view)
                return true
            }
        }
        return false
    }

    private fun createSmegma(resultBitmap: Bitmap, results: Array<Result>, realSize: Size) {
        rlCodeHintContainer?.addView(createCodeSnapshotView(resultBitmap))
        rlCodeHintContainer?.addView(createSmegmaView())
        results.forEach {
            val hintView = createCodeHintView(it, realSize)
            rlCodeHintContainer?.addView(hintView)
            if (scModel.isStartCodeHintAnimation) {
                createCodeAnimeSet(hintView)
            }
        }
        rlParentContent?.addView(rlCodeHintContainer)
    }

    private fun createCodeAnimeSet(view: View) {
        val keyframe1 = Keyframe.ofFloat(0f, 1f)
        val keyframe2 = Keyframe.ofFloat(0.7f, 1f)
        val keyframe3 = Keyframe.ofFloat(0.85f, 0.7f)
        val keyframe4 = Keyframe.ofFloat(1f, 1f)
        val frameHolderX =
            PropertyValuesHolder.ofKeyframe("scaleX", keyframe1, keyframe2, keyframe3, keyframe4)

        val animatorX = ObjectAnimator.ofPropertyValuesHolder(view, frameHolderX)
        animatorX.duration = 2000
        animatorX.repeatCount = ValueAnimator.INFINITE
        animatorX.repeatMode = ValueAnimator.REVERSE

        val frameHolderY =
            PropertyValuesHolder.ofKeyframe("scaleY", keyframe1, keyframe2, keyframe3, keyframe4)

        val animatorY = ObjectAnimator.ofPropertyValuesHolder(view, frameHolderY)
        animatorY.duration = 2000
        animatorY.repeatCount = ValueAnimator.INFINITE
        animatorY.repeatMode = ValueAnimator.REVERSE

        val codeHintAnimeSet = AnimatorSet()
        codeHintAnimeSet.playTogether(animatorX, animatorY)
        // 设置插值器，使动画速度变化更加平滑
        codeHintAnimeSet.interpolator = DecelerateInterpolator()
        codeHintAnimeSet.start()

        mAnimeSetList.add(codeHintAnimeSet)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdownNow()
        baseScanView?.cancelAnim()
        if (mAnimeSetList.isEmpty()){
            return
        }
        mAnimeSetList.forEach {
            it.cancel()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (clearCodeHintContainer()) {
            mScanCodeAnalyzer?.resumeMultiReader()
            return
        }
        super.onBackPressed()
    }

    override fun onBackCode(result: Result) {
        callBackResult(result)
    }

    override fun onBackMultiResultCode(resultBitmap: Bitmap, results: Array<Result>, realSize: Size) {
        runOnUiThread {
            kotlin.Result.runCatching {
                if (results.size == 1) {
                    val result = results[0]
                    callBackResult(result)
                    return@runCatching
                }
                createSmegma(resultBitmap, results, realSize)
            }.onFailure {
                mScanCodeAnalyzer?.resumeMultiReader()
            }
        }
    }
}