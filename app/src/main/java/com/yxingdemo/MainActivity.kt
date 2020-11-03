package com.yxingdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import com.yxing.ScanCodeActivity
import com.yxing.ScanCodeConfig
import com.yxing.def.ScanStyle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnClick.setOnClickListener {
            RxPermissions(this@MainActivity).requestEachCombined(Manifest.permission.CAMERA)
                .subscribe {
                    if(it.granted){
                        ScanCodeConfig.create(this@MainActivity).apply {
                            isPlayAudio = true
                            style = ScanStyle.QQ
                        }.buidler().start(ScanCodeActivity::class.java)
                    }
                }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ScanCodeConfig.QUESTCODE){
            val code = data?.extras?.getString(ScanCodeConfig.CODE_KEY)
            Toast.makeText(this, code, Toast.LENGTH_SHORT).show()
        }
    }
}
