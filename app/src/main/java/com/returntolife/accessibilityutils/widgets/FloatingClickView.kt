package com.returntolife.accessibilityutils.widgets


import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.returntolife.accessibilityutils.ClickInfo
import com.returntolife.accessibilityutils.R
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding
import com.returntolife.accessibilityutils.databinding.ViewFloatingClickBinding


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */

class FloatingClickView(
    context: Context, attrs: AttributeSet? = null
) :
    DragViewGroup(context, attrs) {

     var clickInfo: ClickInfo? = null
         set(value) {
             field = value
             updateClickInfo()
         }

    var whenShowConfigDialog: (() -> Unit)?=null

    var binding:ViewFloatingClickBinding

    init {
        binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)
        binding.root.setBackgroundResource(R.drawable.shape_bg_click)

        initListener()
    }

    private var timeTemp = 0L
    private var clickCount = 0
    private var firstDelay = true

    private fun updateClickInfo() {
        binding.tvName.text = clickInfo?.id.toString()
    }


    fun checkCanClick(): Boolean {
        clickInfo?:return false

        //首次检测先赋当前时间
        if (timeTemp == 0L) {
            timeTemp = System.currentTimeMillis()
        }

        if (clickInfo!!.clickCount != ClickInfo.REVERT && clickCount > clickInfo!!.clickCount) {
            //超过点击次数
            return false
        }



        if (clickInfo!!.firstDelayTime > 0 && firstDelay) {
            //首次延迟检测

            return if (System.currentTimeMillis() - timeTemp < clickInfo!!.firstDelayTime) {
                false
            } else {
                firstDelay = false
                clickCount++
                true
            }
        }

        if (System.currentTimeMillis() - timeTemp > clickInfo!!.interval) {
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
        clickInfo?:return
        whenShowConfigDialog?.invoke()

        val dialogBinding =
            DialogMotifyClickinfoBinding.inflate(LayoutInflater.from(context.applicationContext))
        dialogBinding.etInterval.setText(clickInfo!!.interval.toString())
        dialogBinding.etCount.setText(clickInfo!!.clickCount.toString())
        dialogBinding.etDelayTime.setText(clickInfo!!.firstDelayTime.toString())

//        var text = "按下持续时间(最少50)"
//        var start = text.indexOf('(')
//        var end = text.length
//        var span = ForegroundColorSpan(Color.RED)
//        var spannableString = SpannableString(text)
//        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        dialogBinding.tvPressTimeTip.text = spannableString

        val text = "点击次数(0为无限)"
        val start = text.indexOf('(')
        val end = text.length
        val span = ForegroundColorSpan(Color.RED)
        val spannableString = SpannableString(text)
        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        dialogBinding.tvCLickCountTip.text = spannableString

        val dialog = android.app.AlertDialog.Builder(context.applicationContext)
            .setView(dialogBinding.root)
            .create()

        val flag: Int = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        dialog.window?.setType(flag)
        dialog.show()


        dialogBinding.btnOk.setOnClickListener {
            clickInfo!!.interval = dialogBinding.etInterval.text.toString().toLong()
            clickInfo!!.clickCount = dialogBinding.etCount.text.toString().toInt()
            clickInfo!!.firstDelayTime = dialogBinding.etDelayTime.text.toString().toLong()

            if (clickInfo!!.interval < 0) {
                clickInfo!!.interval = 0
            }

            if (clickInfo!!.clickCount < 0) {
                clickInfo!!.clickCount = 0
            }

            if (clickInfo!!.firstDelayTime < 0) {
                clickInfo!!.firstDelayTime = 0
            }
            LogUtils.d("修改点击配置  clickInfo=${clickInfo}")
            dialog.dismiss()
        }

    }

}