package com.returntolife.accessibilityutils.widgets


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.blankj.utilcode.util.SizeUtils
import com.returntolife.accessibilityutils.GestureWidgetListener
import com.returntolife.accessibilityutils.TimerManager
import com.returntolife.accessibilityutils.tryAddView
import com.returntolife.accessibilityutils.tryRemoveView


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/26/23
 *des:
 *  cn: 滑动手势处理，由于Android 系统 window点击事件是无法跨window传递，所以这里用一个View来绘制滑动背景，避免底部界面无法点击问题
 */
@SuppressLint("ViewConstructor")
class SliderView(context: Context, private val timerManager: TimerManager) :
    View(context), GestureWidgetListener {

    private val mWindowManager: WindowManager
    private val paint = Paint()
    private val path = Path()


    private lateinit var startView: SliderItemView
    private lateinit var centerView: SliderItemView
    private lateinit var endView: SliderItemView

    var whenShowConfigDialog: (() -> Unit)?=null

    init {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        paint.color = Color.RED
        paint.strokeWidth = SizeUtils.dp2px(2f).toFloat()
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (this::startView.isInitialized.not()
            || this::centerView.isInitialized.not()
            || this::endView.isInitialized.not()
        ) {
            return
        }

        canvas?.drawColor(Color.TRANSPARENT)

        val startArray = startView.getViewPos()
        val centerArray = centerView.getViewPos()
        val endArray = endView.getViewPos()
        path.reset()
        path.moveTo(startArray[0].toFloat()-1, startArray[1].toFloat()-1)
        path.quadTo(
            centerArray[0].toFloat()-1,
            centerArray[1].toFloat(),
            endArray[0].toFloat()-1,
            endArray[1].toFloat()
        )

        canvas?.drawPath(path, paint)
    }


    override fun removeWidget() {
        mWindowManager.tryRemoveView(this)

        if(this::startView.isInitialized){
            startView.remove()
        }

        if(this::centerView.isInitialized){
            centerView.remove()
        }

        if(this::endView.isInitialized){
            endView.remove()
        }
    }

    override fun showWidget(left: Int, top: Int) {
        WindowManager.LayoutParams().apply {
            gravity = Gravity.CENTER
            //总是出现在应用程序窗口之上
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }else{
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            }
            //设置图片格式，效果为背景透明
            format = PixelFormat.RGBA_8888

            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

            width = FrameLayout.LayoutParams.MATCH_PARENT
            height = FrameLayout.LayoutParams.MATCH_PARENT
            if (isAttachedToWindow) {
                mWindowManager.tryRemoveView(this@SliderView)
            }
            mWindowManager.tryAddView(this@SliderView, this)
        }


        val listener = {
            invalidate()
        }
        startView = SliderItemView(context, "起").apply {
            onPositionChange = listener
            setOnLongClickByCheck {
                whenShowConfigDialog?.invoke()
                timerManager.showDialog(context)
                true
            }
            show(-SizeUtils.dp2px(16f+16f+32f), 0)
        }

        centerView = SliderItemView(context, "中").apply {
            onPositionChange = listener
            setOnLongClickByCheck {
                whenShowConfigDialog?.invoke()
                timerManager.showDialog(context)
                true
            }
            show(0, 0)
        }

        endView = SliderItemView(context, "终").apply {
            onPositionChange = listener
            setOnLongClickByCheck {
                whenShowConfigDialog?.invoke()
                timerManager.showDialog(context)
                true
            }
            show(SizeUtils.dp2px(16f+16f+32f), 0)
        }

        postDelayed({
            invalidate()
        },100)
    }

    override fun getPath(): Path {
        val sliderPath = Path()
        if (this::startView.isInitialized.not()
            || this::centerView.isInitialized.not()
            || this::endView.isInitialized.not()
        ) {
            return sliderPath
        }

        val startArray = startView.getViewPos()
        val centerArray = centerView.getViewPos()
        val endArray = endView.getViewPos()
        sliderPath.reset()
        sliderPath.moveTo(startArray[0]-2.toFloat(), startArray[1].toFloat())
        sliderPath.lineTo(endArray[0]-2.toFloat(), endArray[1].toFloat())
        sliderPath.quadTo(
            centerArray[0]-2.toFloat(),
            centerArray[1].toFloat(),
            endArray[0]-2.toFloat(),
            endArray[1].toFloat()
        )

        return  sliderPath
    }


}