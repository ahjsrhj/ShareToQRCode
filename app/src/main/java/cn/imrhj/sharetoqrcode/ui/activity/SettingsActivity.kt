package cn.imrhj.sharetoqrcode.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.util.qrCodeSize
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File


class SettingsActivity : AppCompatActivity() {
    private val file by lazy { File(filesDir, "logo.jpg") }
    private val MY_URL = "https://github.com/ahjsrhj/ShareToQRCode"
    private val mSharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }
    private var mUseLogo = false
    private var mBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initUseLogo()
        initQRCode()
        fragmentManager.beginTransaction()
                .replace(R.id.content, SettingFragment())
                .commit()
    }

    private fun initUseLogo() {
        mUseLogo = mSharedPreferences.getBoolean(getString(R.string.pref_key_enable_logo), false)
    }

    private fun initQRCode() {
        if (mBitmap != null) {
            mBitmap?.recycle()
            mBitmap = null
        }
        mBitmap = if (mUseLogo && file.exists()) {
            val logo = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
            QRCodeEncoder.syncEncodeQRCode(MY_URL, qrCodeSize, Color.BLACK, logo)
        } else {
            QRCodeEncoder.syncEncodeQRCode(MY_URL, qrCodeSize)
        }
        if (mBitmap != null) {
            image.setImageBitmap(mBitmap)
        }
    }

    fun showLogo() {
        this.mUseLogo = true
        this.initQRCode()
    }

    fun hideLogo() {
        this.mUseLogo = false
        this.initQRCode()
    }

    fun checkLogo() {
        initUseLogo()
        initQRCode()
    }

    class SettingFragment : PreferenceFragment() {
        private val REQUEST_ALBUM = 11
        private val REQUEST_CROP = 12
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings)
        }

        override fun onStart() {
            super.onStart()
            register()
        }

        private fun register() {
//            findPreference(getString(R.string.pref_key_enable_custom)).setOnPreferenceChangeListener { _, newValue ->
//                Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: $newValue")
//                if (activity is SettingsActivity) {
//                    if (newValue as Boolean) {
//                        (activity as SettingsActivity).checkLogo()
//                    } else {
//                        (activity as SettingsActivity).hideLogo()
//                    }
//                }
//                true
//            }

            findPreference(getString(R.string.pref_key_enable_logo)).setOnPreferenceChangeListener { _, newValue ->
                Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: enable logo $newValue")
                if (activity is SettingsActivity) {
                    if (newValue as Boolean) {
                        (activity as SettingsActivity).showLogo()
                    } else {
                        (activity as SettingsActivity).hideLogo()
                    }
                }
                true
            }

            val qrCustomImgPref = findPreference(getString(R.string.pref_key_custom_img))
            qrCustomImgPref.setOnPreferenceClickListener {
                Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: 选择图片")
                selectImg()
                return@setOnPreferenceClickListener true
            }
        }

        private fun selectImg() {
            val albumIntent = Intent(Intent.ACTION_PICK)
            albumIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(albumIntent, REQUEST_ALBUM)
        }

        private fun cropImage(uri: Uri) {
            val file = File(activity.filesDir, "logo.jpg")
            val intent = Intent(activity, CropImageActivity::class.java)
            intent.data = uri
            intent.putExtra("file", file.absolutePath)
            startActivityForResult(intent, REQUEST_CROP)

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog onActivityResult: requestCode = $requestCode $resultCode $data")
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    REQUEST_ALBUM -> {
                        this.cropImage(data?.data!!)
                    }
                    REQUEST_CROP -> {
                        if (activity is SettingsActivity) {
                            (activity as SettingsActivity).showLogo()
                        }
                    }
                }
            }
        }

    }
}
