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
            ret = src.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            it.close()
        }
    return ret
}

fun isEmptyBitmap(src: Bitmap?): Boolean = src == null || src.width == 0 || src.height == 0
