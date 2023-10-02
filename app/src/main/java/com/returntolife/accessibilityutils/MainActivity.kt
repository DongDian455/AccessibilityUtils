package com.returntolife.accessibilityutils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.ToastUtils
import com.returntolife.accessibilityutils.databinding.ActivityMainBinding
import com.returntolife.accessibilityutils.databinding.DialogAuthCheckBinding
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    init {
        LogUtils.getConfig().globalTag = "ACC-LogUtils"
    }


    private lateinit var binding: ActivityMainBinding

    private val timer = Timer()
    private val authUtils = AuthUtils()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#79AC78")))
        supportActionBar?.title = Html.fromHtml("<font color='#ffffff'>自动化手势工具</font>", 0)

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
            showMenuWindow()
        }


        startCheckService()

//        binding.btnTest1.setOnClickListener {
//            binding.btnTest1.text = (binding.btnTest1.text.toString().toInt()+1).toString()
//        }
//

//        binding.btnTest2.setOnClickListener {
//            binding.btnTest2.text = (binding.btnTest2.text.toString().toInt()+1).toString()
//        }
//
//        binding.btnTest3.setOnClickListener {
//            binding.btnTest3.text = (binding.btnTest3.text.toString().toInt()+1).toString()
//        }
//
//        binding.btnTest4.setOnClickListener {
//            binding.btnTest4.text = (binding.btnTest4.text.toString().toInt()+1).toString()
//        }
//
//        binding.btnTest5.setOnClickListener {
//            binding.btnTest5.text = (binding.btnTest5.text.toString().toInt()+1).toString()
//        }
//
//        binding.btnReset.setOnClickListener {
//            binding.btnTest2.text =  "0"
//            binding.btnTest1.text =  "0"
//            binding.btnTest4.text =  "0"
//            binding.btnTest3.text =  "0"
//            binding.btnTest5.text =  "0"
//        }


        authCheck()
    }

    private fun authCheck(){

        if(authUtils.isAuth()){
            return
        }

        val dialogBinding =
            DialogAuthCheckBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()


        dialogBinding.btnOk.setOnClickListener {

            val key  = dialogBinding.etInterval.text.toString()

            if(key.isEmpty()){
                return@setOnClickListener
            }

            if(authUtils.checkAuth(key)){
                ToastUtils.showShort("验证通过")

                dialog.dismiss()
            }else{
                ToastUtils.showShort("验证失败")
            }
        }



    }

    /**
     * cn : 开始定时检测无障碍服务是否已经启动，启动后就停止检测
     * en : Start to regularly detect whether the accessibility service has been started, and stop the detection after the start
     */
    private fun startCheckService() {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                binding.root.post {
                    val isOpenService = ServiceUtils.isServiceRunning(AutoClickService::class.java)

                    binding.btnStart.isEnabled =
                        isOpenService && Settings.canDrawOverlays(this@MainActivity)
                    if (isOpenService) {
                        timer.cancel()
                    }
                }

            }
        }
        timer.schedule(task, 1000, 1000)
    }

    override fun onResume() {
        super.onResume()

        val isOpenService = ServiceUtils.isServiceRunning(AutoClickService::class.java)

        binding.btnStart.isEnabled = isOpenService && Settings.canDrawOverlays(this)
    }


    private fun showMenuWindow() {
        sendBroadcast(Intent().apply {
            action = AutoClickService.ACTION_SHOW
        })
    }


}