package com.returntolife.accessibilityutils


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding
import com.returntolife.accessibilityutils.databinding.ViewFloatingClickBinding


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
@SuppressLint("ViewConstructor")
class FloatingClickView(
    context: Context,
    val clickInfo: ClickInfo,
    private val whenShowConfigDialog: () -> Unit,
) :
    DragViewGroup(context) {


    init {
        val binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)

        binding.tvName.text = clickInfo.id.toString()

        initListener()
    }

    private var timeTemp = 0L
    private var clickCount = 0
    private var firstDelay = true


    fun checkCanClick(): Boolean {
        //首次检测先赋当前时间
        if (timeTemp == 0L) {
            timeTemp = System.currentTimeMillis()
        }

        if (clickInfo.clickCount != ClickInfo.REVERT && clickCount > clickInfo.clickCount) {
            //超过点击次数
            return false
        }



        if (clickInfo.firstDelayTime > 0 && firstDelay) {
            //首次延迟检测

            return if (System.currentTimeMillis() - timeTemp < clickInfo.firstDelayTime) {
                false
            } else {
                firstDelay = false
                clickCount++
                true
            }
        }

        if (System.currentTimeMillis() - timeTemp > clickInfo.interval) {
            timeTemp = System.currentTimeMillis()
            clickCount++
            return true
        }

        return false
    }

    fun reset() {
        timeTemp = 0
        clickCount = 0
        firstDelay = true
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
        dialogBinding.etDelayTime.setText(clickInfo.firstDelayTime.toString())

        val dialog = android.app.AlertDialog.Builder(context.applicationContext)
            .setView(dialogBinding.root)
            .create()

        val flag: Int = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        dialog.window?.setType(flag)
        dialog.show()


        dialogBinding.btnOk.setOnClickListener {
            clickInfo.interval = dialogBinding.etInterval.text.toString().toLong()
            clickInfo.clickCount = dialogBinding.etCount.text.toString().toInt()
            clickInfo.firstDelayTime = dialogBinding.etDelayTime.text.toString().toLong()
            LogUtils.d("修改点击配置  clickInfo=${clickInfo}")
            dialog.dismiss()
        }

    }

}