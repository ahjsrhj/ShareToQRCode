package cn.imrhj.sharetoqrcode.ui.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.util.Commander
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
        setSupportActionBar(toolbar)
        initView()
        initUseLogo()
        initQRCode()
        fragmentManager.beginTransaction()
                .replace(R.id.content, SettingFragment())
                .commit()
    }

    private fun initView() {
        val param = card.layoutParams
        val width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        param.width = width
        param.height = width
        val borderWidth = dp2px(mSharedPreferences.getInt(getString(R.string.pref_key_border_width), 12))
        image.setPadding(borderWidth, borderWidth, borderWidth, borderWidth)
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

    fun dp2px(dp: Int): Int {
        return (resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    fun changeBoarder(borderWidth: Int) {
        val width = dp2px(borderWidth)
        image.setPadding(width, width, width, width)
    }

    class SettingFragment : PreferenceFragment() {
        private val REQUEST_ALBUM = 11
        private val REQUEST_CROP = 12
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            if (view != null) {
                val listView = view.findViewById<ListView>(android.R.id.list)
                val adapter = listView.adapter
                if (adapter != null) {
                    var height = 0
                    for (i in 0 until adapter.count) {
                        val item = adapter.getView(i, null, listView)
                        item.measure(0, 0)
                        height += item.measuredHeight
                    }
                    val frame = activity.findViewById<FrameLayout>(R.id.content)
                    val param = frame.layoutParams
                    param.height = height + (listView.dividerHeight * adapter.count)
                    frame.layoutParams = param
                }
            }
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

            findPreference(getString(R.string.pref_key_try_root)).setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    val root = Commander.haveRoot()
                    Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: $root")
                    if (root) {
                        return@setOnPreferenceChangeListener true
                    } else {
                        Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: showbar")
                        Toast.makeText(activity, "瞎点啥啊，手机root了么?", Toast.LENGTH_LONG).show()
                        return@setOnPreferenceChangeListener false
                    }
                }
                true
            }

            findPreference(getString(R.string.pref_key_hide_icon)).setOnPreferenceChangeListener { _, newValue ->
                val componentName = ComponentName(activity.applicationContext, LoadingActivity::class.java)
                if (newValue == true) {
                    // hide
                    activity.packageManager.setComponentEnabledSetting(
                            componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP)
                } else {
                    // show
                    activity.packageManager.setComponentEnabledSetting(
                            componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP)
                }
                true
            }

            findPreference(getString(R.string.pref_key_border_width)).setOnPreferenceChangeListener { _, newValue ->
                Log.d(Thread.currentThread().name, "class = SettingFragment rhjlog register: $newValue")
                (activity as SettingsActivity).changeBoarder((newValue as Number).toInt())
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
