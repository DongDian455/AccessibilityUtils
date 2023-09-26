package com.returntolife.accessibilityutils.widgets


import android.R.attr
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.returntolife.accessibilityutils.R


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/26/23
 *des:
 */
class SliderView(context: Context, attrs: AttributeSet? = null) :
    DragInnerViewGroup(context, attrs) {

    val paint = Paint()
    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_slider_arrow)

        val scaleBitmap = ImageUtils.scale(bitmap,SizeUtils.dp2px(17f),SizeUtils.dp2px(17f))
        LogUtils.d("SliderView w=${bitmap.width} h=${bitmap.height}")
        val shader = BitmapShader(scaleBitmap, TileMode.REPEAT, TileMode.REPEAT)
        paint.shader = shader
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

//        if(startView!=null && endView!=null){
//            val startX = startView?.x?:0f
//            val startY = startView?.y?:0f
//
//            val endX = endView?.x?:0f
//            val endY = endView?.y?:0f
//
//            if(endX-startX!=0f){
//                val k=(endY-startY)/(endX-startX)
//                canvas?.rotate(k)
//                canvas?.drawRect(RectF(startX,startY,endX-startX,endY-startX),paint)
//            }
//        }

//        canvas?.rotate(k)
        canvas?.drawRect(RectF(0f,0f,200f,100f),paint)
    }
}