package com.returntolife.accessibilityutils.widgets

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import com.returntolife.accessibilityutils.tryAddView
import com.returntolife.accessibilityutils.tryRemoveView
import com.returntolife.accessibilityutils.tryUpdateView
import kotlin.math.abs

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des: 整个跟着手势移动的viewGroup
 */
open class DragViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {


    protected val mWindowManager: WindowManager
    protected var mParams: WindowManager.LayoutParams? = null

    //按下坐标
     private var mTouchStartX = -1f
     private var mTouchStartY = -1f

    private var downX = -1f
    private var downY = -1f
    protected var isDragging = false

    var onPositionChange:(()->Unit)? = null

    init {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY

                    downX = event.rawX
                    downY = event.rawY
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    mParams?.let {
                        it.x += (event.rawX - mTouchStartX).toInt()
                        it.y += (event.rawY - mTouchStartY).toInt()

                        if(mWindowManager.tryUpdateView(this, it)){
                            onPositionChange?.invoke()
                        }

                        if (abs(event.rawX - downX) > 50 || abs(event.rawY - downY) > 50) {
                            isDragging = true
                        }
                    }
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY

                    return false
                }

                MotionEvent.ACTION_UP -> {

                }

                else -> {

                }
            }
        }

        return super.onTouchEvent(event)
    }




    fun getViewPos(): IntArray {
        val location = IntArray(2)
        getLocationOnScreen(location)
        return location
    }


    fun show(left:Int =0,top:Int=0,gravity: Int = Gravity.CENTER) {
        mParams = WindowManager.LayoutParams()
        mParams?.apply {
            this.gravity = gravity
            //总是出现在应用程序窗口之上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }else{
                type =
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            }
            //设置图片格式，效果为背景透明
            format = PixelFormat.RGBA_8888

            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH

            x = left
            y = top
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            if (isAttachedToWindow) {
                mWindowManager.tryRemoveView(this@DragViewGroup)
            }
            mWindowManager.tryAddView(this@DragViewGroup, this)
        }
    }

     fun remove() {
        mWindowManager.tryRemoveView(this)
    }

}