package cn.imrhj.sharetoqrcode.ui.view

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.support.v7.widget.CardView
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import cn.imrhj.sharetoqrcode.R
import cn.imrhj.sharetoqrcode.util.getCacheDir
import cn.imrhj.sharetoqrcode.util.save
import kotlinx.android.synthetic.main.dialog_image.*
import java.io.File


/**
 * Created by rhj on 2017/9/6.
 */
class ImageDialog(context: Context, bitmap: Bitmap, content: String) : Dialog(context) {

    private var mBitmap: Bitmap = bitmap
    private lateinit var mListener: () -> Unit?
    private var mShawCode = true
    private val mContent = content
//    private val image by lazy { findViewById<ImageView>(R.id.image) }

    private val mClipboardManager by lazy { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)
        val width = (context.resources.displayMetrics.widthPixels * 0.7).toInt()
        val layoutParams = ViewGroup.LayoutParams(width, width)
        setContentView(contentView, layoutParams)
        val text = findViewById<TextView>(R.id.text)
        val card = findViewById<CardView>(R.id.card)
        val scrollView = findViewById<View>(R.id.scrollView)
        text.text = content
        image.setImageBitmap(bitmap)
        image.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                scrollView.visibility = View.VISIBLE
                scrollView.translationX = image.width.toFloat()
            }
        })
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                if (e != null) {
                    showMenu(image, e.x.toInt(), e.y.toInt())
                }
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                val size = card.width.toFloat()
                val animator1 = ObjectAnimator.ofFloat(image, "translationX", if (mShawCode) 0F else -size, if (mShawCode) -size else 0F)
                val animator2 = ObjectAnimator.ofFloat(scrollView, "translationX", if (mShawCode) size else 0F, if (mShawCode) 0F else size)
                animator1.setDuration(300).start()
                animator2.setDuration(300).start()
                mShawCode = !mShawCode
                return super.onSingleTapConfirmed(e)
            }
        })

        card.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
        card.setOnLongClickListener { true }   // 为了震动
        text.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
        text.setOnLongClickListener { true }
    }

    fun updateBorderWidth(borderWidth: Int) {
        image.setPadding(borderWidth, borderWidth, borderWidth, borderWidth)
    }

    private fun showMenu(view: View, x: Int, y: Int) {
        val contentView = View.inflate(context, R.layout.popup_layout, null)
        val popupWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y)

        contentView.findViewById<View>(R.id.tv_share).setOnClickListener { dismissMenu(popupWindow, this::shareBitmap) }
        contentView.findViewById<View>(R.id.tv_exit).setOnClickListener { dismiss() }
        contentView.findViewById<View>(R.id.tv_open).setOnClickListener { dismissMenu(popupWindow, this::openBitmap) }
        contentView.findViewById<View>(R.id.tv_setting).setOnClickListener { dismissMenu(popupWindow, this::openSettingActivity) }
        contentView.findViewById<View>(R.id.tv_copy).setOnClickListener { copy(popupWindow) }
    }

    private fun copy(popupWindow: PopupWindow) {
        popupWindow.dismiss()
        mClipboardManager.primaryClip = ClipData.newPlainText(mContent, mContent)
        Toast.makeText(context, "文本复制成功", Toast.LENGTH_LONG).show()
    }

    fun setOnSettingClickListener(listener: () -> Unit) {
        this.mListener = listener
    }

    private fun openBitmap() {
        val file = saveToFile()
        if (file != null) {
            val intent = buildIntent(file, Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "image/jpg")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "生成临时文件失败", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveToFile(): File? {
        val file = File(getCacheDir(context), "QRCode.jpg")
        val result = save(mBitmap, file)
        return if (result) {
            file
        } else {
            null
        }
    }

    private fun shareBitmap() {
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
        return intent
    }

    private fun dismissMenu(popupWindow: PopupWindow, func: () -> Unit) {
        popupWindow.dismiss()
        func()
    }

    private fun openSettingActivity() {
        mListener()
    }

}