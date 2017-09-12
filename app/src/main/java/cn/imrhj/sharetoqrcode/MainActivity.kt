package cn.imrhj.sharetoqrcode

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.WriterException
import com.google.zxing.BarcodeFormat
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initStatusBar()
        initIntent()
    }

    private fun initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =  Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility
                .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .or(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.decorView.systemUiVisibility = systemUiVisibility

        var decorView = window.decorView as ViewGroup
        var statusBarHeight = getStatusBarHeight()
        var translucentView = View(this)
        var lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
        decorView.addView(translucentView, lp)
        translucentView.setBackgroundColor(Color.argb(0, 0, 0, 0))

    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = resources.getDimensionPixelSize(resId)
        }
        return result
    }

    private fun initIntent() {
        if (intent.type == "text/plain") {
            var shareTxt = intent.getStringExtra(Intent.EXTRA_TEXT)
            showDialog(generateBitmap(shareTxt))
        }
    }

    private fun generateBitmap(string: String) : Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        var size : Int = (resources.displayMetrics.widthPixels * 0.7).toInt()
        try {
            val encodeContent = String(string.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)
            val encode = qrCodeWriter.encode(encodeContent, BarcodeFormat.QR_CODE, size, size)
            val pixels = IntArray(size * size)
            for (i in 0 until size) {
                for (j in 0 until size) {
                    if (encode.get(j, i)) {
                        pixels[i * size+ j] = 0x00000000
                    } else {
                        pixels[i * size+ j] = 0xffffffff.toInt()
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, size, size, size, Bitmap.Config.RGB_565)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return null
    }

    private fun showDialog(bitmap: Bitmap?) {
        if (bitmap != null) {
            val dialog = ImageDialog(this, bitmap)
            dialog.setOnDismissListener {
                finish()
                System.exit(0)
            }
            dialog.show()
        }
    }

}
