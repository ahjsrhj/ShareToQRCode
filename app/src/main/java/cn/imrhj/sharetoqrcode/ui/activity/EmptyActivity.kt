package cn.imrhj.sharetoqrcode.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class EmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
        overridePendingTransition(0, 0)
    }
}
