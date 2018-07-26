package cn.imrhj.sharetoqrcode.util

import cn.imrhj.sharetoqrcode.App


/**
 * Created by rhj on 2017/9/14.
 */

val qrCodeSize by lazy {
    (App.instance().resources.displayMetrics.widthPixels * 0.7).toInt()
}
