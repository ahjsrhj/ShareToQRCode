package cn.imrhj.sharetoqrcode.service

import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.ui.activity.EmptyActivity
import cn.imrhj.sharetoqrcode.ui.activity.MediaProjectionActivity
import cn.imrhj.sharetoqrcode.util.Commander
import cn.imrhj.sharetoqrcode.util.file2bitmap
import java.io.File

@TargetApi(Build.VERSION_CODES.N)
class ScreenQRCodeTile : TileService() {
    private val mSharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(applicationContext) }
    private var mUseRoot = false
    private val mClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onClick() {
        super.onClick()
        mUseRoot = mSharedPreferences.getBoolean(getString(R.string.pref_key_try_root), false)
        if (mUseRoot) {
            scanQRCodeForRoot()
        } else {
            val intent = Intent(this, MediaProjectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityAndCollapse(intent)
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }

    private fun scanQRCodeForRoot() {
        startActivityAndCollapse(Intent(this, EmptyActivity::class.java))
        val bitmap = getScreenshot()
        if (bitmap != null) {
            val result = QRCodeDecoder.syncDecodeQRCode(bitmap)
            val isCopy = mSharedPreferences.getBoolean(getString(R.string.pref_key_save_clip_board), false)
            if (result?.isNotBlank() == true) {
                this.copyClipBoard(result)
                Toast.makeText(baseContext, "${if (isCopy) "已复制到剪贴板:" else "扫描结果:"} $result", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(baseContext, "啊！居然没找到二维码", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(baseContext, "root模式运行失败,转为普通模式", Toast.LENGTH_SHORT).show()
            startActivityAndCollapse(Intent(this, MediaProjectionActivity::class.java))
        }
    }

    private fun copyClipBoard(text: String) {
        mClipboardManager.primaryClip = ClipData.newPlainText(text, text)
    }

    private fun getScreenshot(): Bitmap? {
        SystemClock.sleep(300)
        val localGraphicsFile = File(applicationContext.externalCacheDir, "screen.png")
        val result = Commander.execRootCmdSilent("screencap -p ${localGraphicsFile.absolutePath}")
        if (result != -1) {
            Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog getScreenshot: 截图成功 $localGraphicsFile")
            return file2bitmap(localGraphicsFile)
        }
        return null
    }
}