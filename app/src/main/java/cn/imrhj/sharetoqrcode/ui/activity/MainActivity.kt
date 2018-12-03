package cn.imrhj.sharetoqrcode.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.ui.view.ImageDialog
import cn.imrhj.sharetoqrcode.util.qrCodeSize
import cn.imrhj.sharetoqrcode.util.transparentStatusBar
import java.io.File


class MainActivity : AppCompatActivity() {
    private val file by lazy { File(filesDir, "logo.jpg") }
    private lateinit var mDialog: ImageDialog
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
            var shareTxt = when {
                intent.hasExtra(Intent.EXTRA_TEXT) -> intent.getStringExtra(Intent.EXTRA_TEXT)
                intent.hasExtra(Intent.EXTRA_PROCESS_TEXT) -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                } else {
                    "https://imrhj.cn"
                }
                else -> "https://imrhj.cn"
            }
            val bitmap = if (mSharedPreferences.getBoolean(getString(R.string.pref_key_enable_logo), false).and(file.exists())) {
                val logo = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                QRCodeEncoder.syncEncodeQRCode(shareTxt, qrCodeSize, Color.BLACK, logo)
            } else {
                QRCodeEncoder.syncEncodeQRCode(shareTxt, qrCodeSize)
            }
            showDialog(bitmap, shareTxt)
        }
    }

    override fun onResume() {
        super.onResume()
        mDialog.updateBorderWidth(dp2px(mSharedPreferences.getInt(getString(R.string.pref_key_border_width), 12)))

    }

    fun dp2px(dp: Int): Int {
        return (resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    private fun showDialog(bitmap: Bitmap?, content: String) {
        if (bitmap != null) {
            mDialog = ImageDialog(this, bitmap, content)
            mDialog.setOnDismissListener {
                finish()
                System.exit(0)
            }
            mDialog.setOnSettingClickListener {
                //                startActivity(Intent(this, SettingsActivity::class.java))
                val intent = Intent()
                intent.data = Uri.parse("cn.imrhj.shareqrcode://setting")
                intent.action = Intent.ACTION_VIEW
                startActivity(intent)
            }
            mDialog.show()
        }
    }

}
