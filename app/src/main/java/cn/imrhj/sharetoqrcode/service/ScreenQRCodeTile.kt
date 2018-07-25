package cn.imrhj.sharetoqrcode.service

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import cn.imrhj.sharetoqrcode.ui.activity.MediaProjectionActivity

@TargetApi(Build.VERSION_CODES.N)
class ScreenQRCodeTile : TileService() {
//    val mMediaProjectionManager by lazy {
//        applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//    }

    override fun onClick() {
        super.onClick()
        Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog onClick: " + qsTile?.state)

        startActivityAndCollapse(Intent(this, MediaProjectionActivity::class.java))
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog onTileAdded: ${qsTile?.state}")
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog onStartListening: " + qsTile?.state)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog onBind: $applicationContext")
        return super.onBind(intent)
    }

    override fun onStopListening() {
        super.onStopListening()
        Log.d(Thread.currentThread().name, "class = ScreenQRCodeTile rhjlog onStopListening: " + qsTile?.state)
    }
}