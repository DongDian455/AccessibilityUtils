package com.returntolife.accessibilityutils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
open class DragViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {


    private val mWindowManager: WindowManager
    private var mParams: WindowManager.LayoutParams? = null

    //按下坐标
    private var mTouchStartX = -1f
    private var mTouchStartY = -1f

    init {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    mParams?.let {
                        it.x += (event.rawX - mTouchStartX).toInt()
                        it.y += (event.rawY - mTouchStartY).toInt()

                        mWindowManager.tryUpdateView(this,it)
                    }
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY

                    return  false
                }
            }
        }

        return super.onTouchEvent(event)
    }

    fun getViewPos():IntArray{
        val location = IntArray(2)
        getLocationOnScreen(location)
        return location
    }


    fun show() {
        mParams = WindowManager.LayoutParams()
        mParams?.apply {
            gravity = Gravity.CENTER
            //总是出现在应用程序窗口之上
            type = if (Build.VERSION.SDK_INT >= 26) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //设置图片格式，效果为背景透明
            format = PixelFormat.RGBA_8888

            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH

            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            if (isAttachedToWindow) {
                mWindowManager.tryRemoveView(this@DragViewGroup)
            }
            mWindowManager.tryAddView(this@DragViewGroup, this)
        }
    }

    open fun remove() {
        mWindowManager.tryRemoveView(this)
    }

}