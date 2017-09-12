package cn.imrhj.sharetoqrcode.util

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Log
import java.io.*


/**
 * Created by rhj on 2017/9/12.
 */

fun save(src: Bitmap, file: File): Boolean {
    if (isEmptyBitmap(src) || !createOrExistsFile(file)) {
        return false
    }

    var ret = false
        FileOutputStream(file).use {
            Log.d("${Thread.currentThread().name} class = save", "rhjlog save: save")
            ret = src.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            it.close()
        }
    return ret
}

private fun isEmptyBitmap(src: Bitmap?): Boolean {
    return src == null || src.width == 0 || src.height == 0
}
