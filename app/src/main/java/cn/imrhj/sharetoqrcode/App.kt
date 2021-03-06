package cn.imrhj.sharetoqrcode

import android.annotation.SuppressLint
import android.app.Application

/**
 * Created by rhj on 2017/9/14.
 */
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: Application? = null

        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}