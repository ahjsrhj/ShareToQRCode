package cn.imrhj.sharetoqrcode.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.ui.view.ImageDialog
import cn.imrhj.sharetoqrcode.util.qrCodeSize
import cn.imrhj.sharetoqrcode.util.transparentStatusBar
import java.io.File


class MainActivity : AppCompatActivity() {
    private val file by lazy { File(filesDir, "logo.jpg") }
    private val mSharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transparentStatusBar(this)
        initIntent()
    }

    private fun initIntent() {
        if (intent.type == "text/plain") {
            var shareTxt = intent.getStringExtra(Intent.EXTRA_TEXT)
            val bitmap = if (mSharedPreferences.getBoolean(getString(R.string.pref_key_enable_logo), false).and(file.exists())) {
                val logo = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                QRCodeEncoder.syncEncodeQRCode(shareTxt, qrCodeSize, Color.BLACK, logo)
            } else {
                QRCodeEncoder.syncEncodeQRCode(shareTxt, qrCodeSize)
            }
            showDialog(bitmap, shareTxt)
        }
    }

    private fun showDialog(bitmap: Bitmap?, content: String) {
        if (bitmap != null) {
            val dialog = ImageDialog(this, bitmap, content)
            dialog.setOnDismissListener {
                finish()
                System.exit(0)
            }
            dialog.setOnSettingClickListener {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            dialog.show()
        }
    }

}
