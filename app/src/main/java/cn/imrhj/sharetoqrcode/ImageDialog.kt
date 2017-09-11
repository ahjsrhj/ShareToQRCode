package cn.imrhj.sharetoqrcode

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow

/**
 * Created by rhj on 2017/9/6.
 */
class ImageDialog constructor(context: Context, bitmap: Bitmap) : Dialog(context) {

    private fun shareBitmap() {

    }

    private fun saveBitmap() {

    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)
        val size = (context.resources.displayMetrics.widthPixels * 0.7).toInt()
        val layoutParams = ViewGroup.LayoutParams(size, size)
        setContentView(contentView, layoutParams)
        val imageView = contentView.findViewById<ImageView>(R.id.image)
        imageView.setImageBitmap(bitmap)
//        val gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {
//            override fun onLongPress(e: MotionEvent?) {
//                if (e != null) {
//                    showMenu(imageView, e.x.toInt(), e.y.toInt())
//                }
//            }
//        })
//        imageView.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }

    private fun showMenu(view: View, x: Int, y: Int) {
        val contentView = View.inflate(context, R.layout.popup_layout, null)
        val popupWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y)

        contentView.findViewById<View>(R.id.tv_save).setOnClickListener { saveBitmap() }
        contentView.findViewById<View>(R.id.tv_share).setOnClickListener { shareBitmap() }
        contentView.findViewById<View>(R.id.tv_exit).setOnClickListener { dismiss() }
    }
}