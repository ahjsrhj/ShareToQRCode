package cn.imrhj.sharetoqrcode

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import cn.imrhj.sharetoqrcode.util.getCacheDir
import cn.imrhj.sharetoqrcode.util.save
import java.io.File


/**
 * Created by rhj on 2017/9/6.
 */
class ImageDialog constructor(context: Context, bitmap: Bitmap) : Dialog(context) {
    private var mBitmap: Bitmap = bitmap
    private var mPopupWindow: PopupWindow? = null

    private fun saveToFile() :File? {
        val file = File(getCacheDir(context), "QRCode.jpg")
        val result = save(mBitmap, file)
        return if (result) {
            file
        } else {
            null
        }
    }

    private fun shareBitmap() {
        mPopupWindow?.dismiss()
        val file = saveToFile()
        if (file != null) {
            context.startActivity(Intent.createChooser(buildIntent(file), "分享二维码"))
        } else {
            Toast.makeText(context, "生成临时文件失败", Toast.LENGTH_LONG).show()
        }
    }

    private fun buildIntent(file: File, action: String = Intent.ACTION_SEND): Intent {
        val intent = Intent()
        intent.action = action
        intent.type = "image/jpg"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        return  intent
    }

    private fun openBitmap() {
        mPopupWindow?.dismiss()
        val file = saveToFile()
        if (file != null) {
            val intent = buildIntent(file, Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "image/jpg")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "生成临时文件失败", Toast.LENGTH_LONG).show()
        }
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
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                if (e != null) {
                    showMenu(imageView, e.x.toInt(), e.y.toInt())
                }
            }
        })
        imageView.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
        imageView.setOnLongClickListener { true }   // 为了震动
    }

    private fun showMenu(view: View, x: Int, y: Int) {
        val contentView = View.inflate(context, R.layout.popup_layout, null)
        mPopupWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mPopupWindow?.showAtLocation(view, Gravity.NO_GRAVITY, x, y)

        contentView.findViewById<View>(R.id.tv_share).setOnClickListener { shareBitmap() }
        contentView.findViewById<View>(R.id.tv_exit).setOnClickListener { dismiss() }
        contentView.findViewById<View>(R.id.tv_open).setOnClickListener { openBitmap() }
    }

}