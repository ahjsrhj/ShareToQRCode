package cn.imrhj.sharetoqrcode.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
        overridePendingTransition(0, 0)
    }
}
