package com.returntolife.accessibilityutils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.LogUtils.Config
import com.blankj.utilcode.util.ServiceUtils
import com.returntolife.accessibilityutils.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    init {
        LogUtils.getConfig().globalTag = "ACC-LogUtils"
    }


    lateinit var binding: ActivityMainBinding

    private var mLayout: FrameLayout? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.btnOpenAcc.setOnClickListener {
            if (ServiceUtils.isServiceRunning(AutoClickService::class.java)) {
                Toast.makeText(this, "已开启", Toast.LENGTH_SHORT).show()
            } else {
                val localIntent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
                startActivity(localIntent)
            }
        }

        binding.btnOpenDialog.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "已开启", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
            }
        }

        binding.btnStart.setOnClickListener {
            showFloatingWindow(1000)
        }

        binding.btnTest1.setOnClickListener {
            binding.btnTest1.text = (binding.btnTest1.text.toString().toInt()+1).toString()
        }

        binding.btnTest2.setOnClickListener {
            binding.btnTest2.text = (binding.btnTest2.text.toString().toInt()+1).toString()
        }
    }

    override fun onResume() {
        super.onResume()

        val isOpenService = ServiceUtils.isServiceRunning(AutoClickService::class.java)

        binding.btnStart.isEnabled = isOpenService && Settings.canDrawOverlays(this)
    }


    private fun showFloatingWindow(interval: Long) {
        sendBroadcast(Intent().apply {
            action = BroadcastConstants.BROADCAST_ACTION_AUTO_CLICK
            putExtra(BroadcastConstants.KEY_ACTION, AutoClickService.ACTION_SHOW)
            putExtra(BroadcastConstants.KEY_INTERVAL, interval)
        })
    }

}