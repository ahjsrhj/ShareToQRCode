package cn.imrhj.sharetoqrcode.ui.activity

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.imrhj.sharetoqrcode.service.CaptureScreenService
import cn.imrhj.sharetoqrcode.util.transparentStatusBar

class MediaProjectionActivity : AppCompatActivity() {
    private val mMediaProjectionManager by lazy {
        applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }
    private val REQUEST_MEDIA_PROJECTION = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar(this)
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
        Log.d(Thread.currentThread().name, "class = MediaProjectionActivity rhjlog onCreate: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            // 获取数据成功，启动service
            val intent = Intent(this, CaptureScreenService::class.java)
            intent.putExtra("data", data)
            intent.putExtra("resultCode", resultCode)
            startService(intent)
            finish()
        }
    }
}
