package com.returntolife.accessibilityutils.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.returntolife.accessibilityutils.databinding.ViewSliderBinding


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/26/23
 *des: 内部子view可以任意拖拽的viewgroup
 */
open class DragInnerViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /**
     * 回调类
     */
    private val callback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 当前触摸的子View
         * @param pointerId
         * @return true:捕获并解析  false:不处理
         */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        /**
         * 当view被开始捕获和解析的回调
         * @param capturedChild 当前被捕获的子view
         * @param activePointerId
         */
        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候
         * view缓慢移动的动画时间的计算; 最好不要返回0
         * @param child
         * @return
         */
        override fun getViewHorizontalDragRange(child: View): Int {
            return measuredWidth - child.measuredWidth
        }

        /**
         * 获取view垂直方向的拖拽范围,目前不能限制边界，最好不要返回0
         * @param child
         * @return
         */
        override fun getViewVerticalDragRange(child: View): Int {
            return measuredHeight - child.measuredHeight
        }

        /**
         * 控制child在水平方向的移动
         * @param child 当前触摸的子View
         * @param left 当前child的即将移动到的位置,left=chile.getLeft()+dx
         * @param dx 本次child水平方向移动的距离
         * @return 表示你真正想让child的left变成的值
         */
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var left = left
            if (left < 0) {
                left = 0 //限制左边界
            } else if (left > measuredWidth - child.measuredWidth) {
                left = measuredWidth - child.measuredWidth //限制右边界
            }
            // return left-dx; //不能移动
            return left
        }

        /**
         * 控制child在垂直方向的移动
         * @param child
         * @param top  top=child.getTop()+dy
         * @param dy
         * @return
         */
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var top = top
            if (top < 0) {
                top = 0 //限制上边界
            } else if (top > measuredHeight - child.measuredHeight) {
                top = measuredHeight - child.measuredHeight //限制下边界
            }
            return top
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View跟随该view移动
         * @param changedView 当前位置改变的child
         * @param left child当前最新的left
         * @param top child当前最新的top
         * @param dx 本次水平移动的距离
         * @param dy 本次垂直移动的距离
         */
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)

        }

        /**
         * 手指抬起的执行该方法
         * @param releasedChild 当前抬起的view
         * @param xvel x方向的移动速度有 正：向右移动
         * @param yvel 方向的移动速度
         */
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)

        }
    }

    init {
        val binding = ViewSliderBinding.inflate(LayoutInflater.from(context), this)
        binding.root.setBackgroundColor(Color.RED)
    }

    /**
     * ViewDragHelper:它主要用于处理ViewGroup中对子View的拖拽处理,
     * 本质是对触摸事件的解析类;
     */
    private var mViewDragHelper: ViewDragHelper = ViewDragHelper.create(this, callback)

     var startView: View? = null

     var endView: View? = null


    /**
     * 当DragLayout的布局文件的结束标签读取完成后会执行该方法，此时会知道自己有几个子控件
     * 一般用来初始化子控件的引用
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        startView = getChildAt(0)
        endView = getChildAt(1)
    }

    /**
     * 确定控件位置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //放在左上角
        val left = paddingLeft
        val top = paddingTop

        //放在水平居中
        //int left = getPaddingLeft() + getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;

        //摆放在左上角
        startView!!.layout(
            left, top, left + startView!!.measuredWidth,
            top + startView!!.measuredHeight
        )

        //摆放在redView下面
        endView!!.layout(
            left, startView!!.bottom, left + endView!!.measuredWidth,
            startView!!.bottom + endView!!.measuredHeight
        )

        invalidate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 让ViewDragHelper帮我们判断是否应该拦截
        return mViewDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 将触摸事件交给ViewDragHelper来解析处理
        event?.let {
            mViewDragHelper.processTouchEvent(event)
        }
        return true
    }



    override fun computeScroll() {
        //如果动画还没结束
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this) //刷新
        }
    }


}

