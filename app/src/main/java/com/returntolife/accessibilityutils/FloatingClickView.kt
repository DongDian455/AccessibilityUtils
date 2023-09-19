package com.returntolife.accessibilityutils


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding
import com.returntolife.accessibilityutils.databinding.ViewFloatingClickBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class FloatingClickView(private val mContext: Context, val clickInfo: ClickInfo) :
    DragViewGroup(mContext) {


    private var scope: CoroutineScope? = null

    var clickListener: ((Float, Float, ClickInfo) -> Unit)? = null

    init {
        val binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)

        binding.tvName.text = clickInfo.id.toString()

        initListener()
    }

    private var timeTemp = 0L
    private var clickCount = 1

    private suspend fun checkClick() {
        //每隔16.6毫秒检测一次,即最多1秒点击60次
        delay(17)

        if (System.currentTimeMillis() - timeTemp > clickInfo.interval) {
            val intArray = getViewPos()
            clickListener?.invoke(intArray[0].toFloat(), intArray[1].toFloat(), clickInfo)
            LogUtils.d("点击 id=${clickInfo.id} count =${clickCount++}")
            timeTemp = System.currentTimeMillis()
        }

        checkClick()
    }

    fun startAutoClick() {
        scope?.cancel()
        scope = MainScope()
        scope?.launch(Dispatchers.IO) {
            checkClick()
        }
    }

    fun stopAutoClick() {
        scope?.cancel()
    }


    override fun remove() {
        stopAutoClick()
        super.remove()

    }

    private fun initListener() {
        setOnLongClickListener {
            showDialog()
            true
        }
    }


    private fun showDialog() {
        val dialogBinding =
            DialogMotifyClickinfoBinding.inflate(LayoutInflater.from(mContext.applicationContext))
        dialogBinding.etInterval.setText(clickInfo.interval.toString())
        dialogBinding.etCount.setText(clickInfo.clickCount.toString())

        val dialog = android.app.AlertDialog.Builder(mContext.applicationContext)
            .setView(dialogBinding.root)
            .create()

        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        dialog.window?.setType(flag)
        dialog.show()


        dialogBinding.btnOk.setOnClickListener {
            clickInfo.interval = dialogBinding.etInterval.text.toString().toInt()
            clickInfo.clickCount = dialogBinding.etCount.text.toString().toInt()

            dialog.dismiss()
        }

    }

}