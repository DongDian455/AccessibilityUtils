package com.returntolife.accessibilityutils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.blankj.utilcode.util.LogUtils


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class AutoClickService : AccessibilityService() {
    companion object {

        //打开悬浮窗
        const val ACTION_SHOW = "action_show"

//        //关闭悬浮窗
//        val ACTION_CLOSE = "action_close"
    }

    private val broadcastReceiver = BroadcastHandler(this)



    /**是否执行完成*/
    private var _isCompleted: Boolean = true

    private lateinit var menuManager: MenuManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.d("onServiceConnected")

        broadcastReceiver.register()
        menuManager = MenuManager(this){
            autoExecution(it)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        broadcastReceiver.unregister()
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        LogUtils.v("onAccessibilityEvent eventType=" + event?.eventType)
    }

    override fun onInterrupt() {

    }


    private fun autoExecution(path: List<AutoGestureInfo>){
        if (_isCompleted.not()) {
            //限制一下，不然会触发点击取消，导致每次都无法成功点击
            LogUtils.w("上一个 滑动手势未完成 ")
            return
        }
        _isCompleted = false
        val gestureDescriptionBuild = GestureDescription.Builder()

        path.forEach {
            gestureDescriptionBuild.addStroke(
                GestureDescription.StrokeDescription(
                    it.gestureWidgetListener.getPath(),
                    0L,
                    it.timerManager.gestureTimerInfo.pressTime
                )
            )
        }

        dispatchGesture(
            gestureDescriptionBuild.build(),
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    LogUtils.d("自动点击完成 ")
                    _isCompleted = true
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    LogUtils.d("自动滑动取消 ")
                    _isCompleted = true
                }
            },
            null
        )
    }


//    private fun autoClickView(list: ArrayList<GestureInfo>) {
//        if (_isCompleted.not()) {
//            //限制一下，不然会触发点击取消，导致每次都无法成功点击
//            LogUtils.w("上一个手势未完成 ")
//            return
//        }
//        _isCompleted = false
//
//        val gestureDescriptionBuild = GestureDescription.Builder()
//
//        val path = Path()
//        list.forEach {
//            path.moveTo(it.posX, it.posY)
//            gestureDescriptionBuild.addStroke(
//                GestureDescription.StrokeDescription(
//                    path,
//                    0L,
//                    it.gestureTimerInfo.pressTime
//                )
//            )
//        }
//
//        dispatchGesture(
//            gestureDescriptionBuild.build(),
//            object : GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    super.onCompleted(gestureDescription)
////                    LogUtils.d("自动点击完成 ")
//                    _isCompleted = true
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    LogUtils.d("自动点击取消 ")
//                    _isCompleted = true
//                }
//            },
//            null
//        )
//
//    }
//

    private inner class BroadcastHandler(val context: Context) : BroadcastReceiver() {

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun register() {
            context.registerReceiver(
                this,
                IntentFilter().apply {
                    addAction(ACTION_SHOW)
                    //息屏关闭自动点击事件
                    addAction(Intent.ACTION_SCREEN_OFF)
                }
            )
        }

        fun unregister() {
            context.unregisterReceiver(this)
        }

        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.apply {
                when (action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        menuManager.removeAll()
                        menuManager.showMenuManager()
                    }
                    ACTION_SHOW ->{
                        menuManager.removeAll()
                        menuManager.showMenuManager()
                    }
                }
            }
        }
    }

}