package com.returntolife.accessibilityutils


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
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
@SuppressLint("ViewConstructor")
class FloatingClickView(
    context: Context,
    private val clickInfo: ClickInfo,
    private val clickListener: (Float, Float, ClickInfo) -> Unit,
    private val whenShowConfigDialog: () -> Unit,
) :
    DragViewGroup(context) {


    private var scope: CoroutineScope? = null


    init {
        val binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)

        binding.tvName.text = clickInfo.id.toString()

        initListener()
    }

    private var timeTemp = 0L
    private var clickCount = 1

    private var firstDelay = true

    private suspend fun checkClick() {
        //每隔16.6毫秒检测一次,即最多1秒点击60次
        if (clickInfo.clickCount != ClickInfo.REVERT && clickCount > clickInfo.clickCount) {
            LogUtils.d("超过点击次数 clickInfo=${clickInfo}")
            return
        }

        if (firstDelay) {
            firstDelay = false
            delay(clickInfo.firstDelayTime)
        }

        if (System.currentTimeMillis() - timeTemp > clickInfo.interval) {
            val intArray = getViewPos()
            clickListener.invoke(intArray[0].toFloat(), intArray[1].toFloat(), clickInfo)
            LogUtils.d("触发点击 clickInfo=${clickInfo} count =${clickCount++}")
            timeTemp = System.currentTimeMillis()
        }

        delay(17)

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
        timeTemp = 0
    }


    override fun remove() {
        stopAutoClick()
        super.remove()
    }

    private fun initListener() {
        setOnLongClickListener {
            if (isDragging.not()) {
                showDialog()
                true
            } else {
                false
            }
        }
    }


    private fun showDialog() {
        whenShowConfigDialog.invoke()

        val dialogBinding =
            DialogMotifyClickinfoBinding.inflate(LayoutInflater.from(context.applicationContext))
        dialogBinding.etInterval.setText(clickInfo.interval.toString())
        dialogBinding.etCount.setText(clickInfo.clickCount.toString())

        val dialog = android.app.AlertDialog.Builder(context.applicationContext)
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
            LogUtils.d("修改点击配置  clickInfo=${clickInfo}")
            dialog.dismiss()
        }

    }

}