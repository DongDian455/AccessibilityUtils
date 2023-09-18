package com.returntolife.accessibilityutils

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
import android.view.View
import android.view.WindowManager


fun WindowManager.tryAddView(view: View, params: WindowManager.LayoutParams): Boolean{
    try {
        addView(view, params)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false
}

fun WindowManager.tryRemoveView(view: View): Boolean {
    try {
      removeView(view)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false
}

fun WindowManager.tryUpdateView(view: View, params: WindowManager.LayoutParams): Boolean {
    try {
        updateViewLayout(view, params)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false
}
