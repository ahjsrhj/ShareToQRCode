package cn.imrhj.sharetoqrcode.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.util.image2bitmap

class CaptureScreenService : Service() {
    private val mSharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    private val mClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val mMediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }
    private var mMediaProjection: MediaProjection? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", 0)
        val resultData = intent?.getParcelableExtra<Intent>("data")
        if (resultCode != null) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, resultData)
        }
        virtualDisplay()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun virtualDisplay() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val windowPoint = Point()
        windowManager.defaultDisplay.getSize(windowPoint)
        val windowWidth = windowPoint.x
        val windowHeight = windowPoint.y
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val screenDensity = metrics.densityDpi
        val imageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 1)


        val mediaProjection = mMediaProjection?.createVirtualDisplay(
                "screen-mirror",
                windowWidth,
                windowHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface,
                null, null)

        imageReader.setOnImageAvailableListener({
            Log.d(Thread.currentThread().name, "class = CaptureScreenService rhjlog virtualDisplay: onImageAvalable")
            try {
                SystemClock.sleep(300)
                val image = it.acquireLatestImage()
                val result = QRCodeDecoder.syncDecodeQRCode(image2bitmap(image))
                val isCopy = mSharedPreferences.getBoolean(getString(R.string.pref_key_save_clip_board), false)
                if (result?.isNotBlank() == true) {
                    this.copyClipBoard(result)
                    Toast.makeText(baseContext, "${if (isCopy) "已复制到剪贴板:" else "扫描结果:"} $result", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(baseContext, "啊！居然没找到二维码", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                imageReader.close()
                mediaProjection?.release()
            }

        }, null)
    }

    private fun copyClipBoard(text: String) {
        mClipboardManager.primaryClip = ClipData.newPlainText(text, text)
    }
}
