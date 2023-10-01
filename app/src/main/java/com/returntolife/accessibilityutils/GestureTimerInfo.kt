package com.returntolife.accessibilityutils

import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */


data class AutoGestureInfo(
    val gestureWidgetListener:GestureWidgetListener,
    val timerManager:TimerManager
)


class TimerManager(val gestureTimerInfo: GestureTimerInfo) {
    private var timeTemp = 0L
    private var clickCount = 0
    private var firstDelay = true


    fun checkCanDispatchGesture(): Boolean {
        //首次检测先赋当前时间
        if (timeTemp == 0L) {
            timeTemp = System.currentTimeMillis()
        }

        if (gestureTimerInfo.count != GestureTimerInfo.REVERT && clickCount > gestureTimerInfo.count) {
            //超过点击次数
            return false
        }



        if (gestureTimerInfo.firstDelayTime > 0 && firstDelay) {
            //首次延迟检测

            return if (System.currentTimeMillis() - timeTemp < gestureTimerInfo.firstDelayTime) {
                false
            } else {
                firstDelay = false
                clickCount++
                true
            }
        }

        if (System.currentTimeMillis() - timeTemp > gestureTimerInfo.interval) {
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

    fun showDialog(context: Context) {


        val dialogBinding =
            DialogMotifyClickinfoBinding.inflate(LayoutInflater.from(context.applicationContext))
        dialogBinding.etInterval.setText(gestureTimerInfo.interval.toString())
        dialogBinding.etCount.setText(gestureTimerInfo.count.toString())
        dialogBinding.etDelayTime.setText(gestureTimerInfo.firstDelayTime.toString())
        dialogBinding.etPressTimer.setText(gestureTimerInfo.pressTime.toString())

//        var text = "按下持续时间(最少50)"
//        var start = text.indexOf('(')
//        var end = text.length
//        var span = ForegroundColorSpan(Color.RED)
//        var spannableString = SpannableString(text)
//        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        dialogBinding.tvPressTimeTip.text = spannableString

        val text = "执行次数(0为无限)"
        val start = text.indexOf('(')
        val end = text.length
        val span = ForegroundColorSpan(Color.RED)
        val spannableString = SpannableString(text)
        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        dialogBinding.tvCLickCountTip.text = spannableString

        val dialog = android.app.AlertDialog.Builder(context.applicationContext)
            .setView(dialogBinding.root)
            .create()

        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        dialog.window?.setType(flag)
        dialog.show()


        dialogBinding.btnOk.setOnClickListener {

            gestureTimerInfo.interval = dialogBinding.etInterval.text.toString().toLong()
            gestureTimerInfo.count = dialogBinding.etCount.text.toString().toInt()
            gestureTimerInfo.firstDelayTime = dialogBinding.etDelayTime.text.toString().toLong()
            gestureTimerInfo.pressTime = dialogBinding.etPressTimer.text.toString().toLong()

            if (gestureTimerInfo.interval < 0) {
                gestureTimerInfo.interval = 0
            }

            if (gestureTimerInfo.count < 0) {
                gestureTimerInfo.count = 0
            }

            if (gestureTimerInfo.firstDelayTime < 0) {
                gestureTimerInfo.firstDelayTime = 0
            }

            if (gestureTimerInfo.pressTime < 30 && gestureTimerInfo.type==GestureType.Click) {
                gestureTimerInfo.pressTime = 30
            }else if (gestureTimerInfo.pressTime < 300 && gestureTimerInfo.type==GestureType.Slider) {
                gestureTimerInfo.pressTime = 300
            }

            LogUtils.d("修改手势配置  clickInfo=${gestureTimerInfo}")
            dialog.dismiss()
        }

    }
}


/**
 *  cn : 手势处理信息
 *  [interval] :  触发间隔
 *  [firstDelayTime] : 首次延迟时间
 *  [pressTime] : 持续时间
 *  [count] ： 执行次数
 */
data class GestureTimerInfo(
    val id: Int,
    val type:GestureType,
    var interval: Long = DEFAULT_INTERVAL,
    var firstDelayTime: Long = DEFAULT_FIRST_DELAY_TIME,
    var pressTime: Long = DEFAULT_PRESS_TIME,
    var count: Int = REVERT,
) {

    companion object {
        //0表示无限次
        const val REVERT = 0

        const val DEFAULT_PRESS_TIME = 50L
        const val DEFAULT_FIRST_DELAY_TIME = 100L
        const val DEFAULT_INTERVAL = 1000L

    }
}


enum class GestureType{
    Click,
    Slider,
    Task
}



