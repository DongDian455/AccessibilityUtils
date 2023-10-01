package com.returntolife.accessibilityutils

import android.graphics.Path


interface GestureWidgetListener {
    fun removeWidget()

    fun showWidget(left:Int = 0,top:Int = 0)

    fun getPath(): Path
}

//interface TimerInfoListener{
//    fun checkCanDispatchGesture():Boolean
//
//    fun reset()
//
//    fun getPath(): Path
//}

