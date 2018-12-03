package cn.imrhj.sharetoqrcode.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import cn.imrhj.sharetoqrcode.R
import kotlinx.android.synthetic.main.activity_cropimage.*
import java.io.File

class CropImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropimage)
        cropImageView.setImageUriAsync(intent.data)
        cropImageView.setAspectRatio(1, 1)

        cropImageView.setOnCropImageCompleteListener { _, result ->
            Log.d(Thread.currentThread().name, "class = CropImageActivity rhjlog onCreate: ${result.uri}")
            val intent = Intent()
            intent.putExtra("uri", result.uri.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        cropButton.setOnClickListener { cropImageView.saveCroppedImageAsync(Uri.fromFile(File(intent.getStringExtra("file")))) }
    }
}
