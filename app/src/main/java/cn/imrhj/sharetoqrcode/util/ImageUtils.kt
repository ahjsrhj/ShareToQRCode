package cn.imrhj.sharetoqrcode.util

import android.graphics.Bitmap
import android.media.Image
import cn.imrhj.sharetoqrcode.App
import java.io.File
import java.io.FileOutputStream


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

fun saveTmpBitmap(src: Bitmap): Boolean {
    val file = File(App.instance().externalCacheDir, "${System.currentTimeMillis()}.jpg")
    return save(src, file)
}

fun image2bitmap(image: Image): Bitmap {
    val width = image.width
    val height = image.height
    val planes = image.planes
    val buffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * width;
    var bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(buffer)
    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
    saveTmpBitmap(bitmap)
    image.close()
    return bitmap
}