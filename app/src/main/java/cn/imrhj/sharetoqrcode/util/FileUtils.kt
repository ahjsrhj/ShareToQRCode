package cn.imrhj.sharetoqrcode.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException


/**
 * Created by rhj on 2017/9/12.
 */
fun createOrExistsFile(file: File?): Boolean {
    if (file == null) {
        return false
    }
    // 如果存在，是文件则返回true，是目录则返回false
    if (file.exists()) {
        return file.isFile()
    }
    if (!createOrExistsDir(file.parentFile)) {
        return false

    }
    return try {
        file.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }


}


fun createOrExistsDir(file: File?): Boolean {
    // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
    if (file == null) {
        return false
    }

    return if (file.exists()) {
        file.isDirectory
    } else {
        file.mkdirs()
    }

}

fun getCacheDir(context: Context): File {
    val cacheDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            || !Environment.isExternalStorageRemovable()) {
        context.externalCacheDir
    } else {
        context.cacheDir
    }

    return if (createOrExistsDir(cacheDir)) {
        cacheDir
    } else {
        context.cacheDir
    }
}