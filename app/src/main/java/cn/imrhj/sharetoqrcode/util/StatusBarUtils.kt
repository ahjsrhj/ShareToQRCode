package cn.imrhj.sharetoqrcode.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

fun transparentStatusBar(activity: Activity) {
    val window = activity.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    var systemUiVisibility = window.decorView.systemUiVisibility
    systemUiVisibility = systemUiVisibility
            .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            .or(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    window.decorView.systemUiVisibility = systemUiVisibility

    var decorView = window.decorView as ViewGroup
    var statusBarHeight = getStatusBarHeight(activity.resources)
    var translucentView = View(activity)
    var lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
    decorView.addView(translucentView, lp)
    translucentView.setBackgroundColor(Color.argb(0, 0, 0, 0))

}

private fun getStatusBarHeight(resources: Resources): Int {
    var result = 0
    val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        result = resources.getDimensionPixelSize(resId)
    }
    return result
}
