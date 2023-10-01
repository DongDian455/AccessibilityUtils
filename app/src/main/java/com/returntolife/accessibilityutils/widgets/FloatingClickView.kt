package com.returntolife.accessibilityutils.widgets


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.returntolife.accessibilityutils.GestureWidgetListener
import com.returntolife.accessibilityutils.R
import com.returntolife.accessibilityutils.TimerManager
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding
import com.returntolife.accessibilityutils.databinding.ViewFloatingClickBinding


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */

@SuppressLint("ViewConstructor")
class FloatingClickView(private val timerInfo: TimerManager,
                        context: Context, attrs: AttributeSet? = null
) :
    DragViewGroup(context, attrs),GestureWidgetListener {


    var whenShowConfigDialog: (() -> Unit)?=null

    private var binding:ViewFloatingClickBinding

    init {
        binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)
        binding.root.setBackgroundResource(R.drawable.shape_bg_click)
        binding.tvName.text = timerInfo.gestureTimerInfo.id.toString()
        initListener()
    }

    override fun removeWidget() {
        remove()
    }

    override fun showWidget(left: Int, top: Int) {
        show(left,top)
    }



    override fun getPath(): Path {
        val path = Path()

        getViewPos().let {
            //cn: 减1是为了避免自己点击自己
            path.moveTo(it[0].toFloat()-1, it[1].toFloat()-1)
        }

        return path
    }


    private fun initListener() {
        setOnLongClickListener {
            if (isDragging.not()) {
                whenShowConfigDialog?.invoke()
                timerInfo.showDialog(context)
                true
            } else {
                false
            }
        }
    }


}