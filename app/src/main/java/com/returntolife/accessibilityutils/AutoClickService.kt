package com.returntolife.accessibilityutils

import android.R.attr
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class AutoClickService : AccessibilityService() {
    companion object {

        //打开悬浮窗
        val ACTION_SHOW = "action_show"

        //自动点击事件 开启/关闭
        val ACTION_PLAY = "action_play"
        val ACTION_STOP = "action_stop"

        //关闭悬浮窗
        val ACTION_CLOSE = "action_close"
    }

    private val broadcastReceiver = BroadcastHandler(this)


    //点击坐标xy
    private var mPointX = -1f
    private var mPointY = -1f

    /**是否执行完成*/
    private var _isCompleted: Boolean = true

    private lateinit var menuManager: MenuManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.d("onServiceConnected")

        broadcastReceiver.register()
        menuManager = MenuManager(this) {
            autoClickView(it)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        broadcastReceiver.unregister()
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        LogUtils.v("onAccessibilityEvent eventType=" + event?.eventType)
    }

    override fun onInterrupt() {

    }


    private fun autoClickView(list: ArrayList<GestureInfo>) {
        if (_isCompleted.not()) {
            //限制一下，不然会触发点击取消，导致每次都无法成功点击
            LogUtils.w("上一个手势未完成 ")
            return
        }
        _isCompleted = false

        val gestureDescriptionBuild = GestureDescription.Builder()

        val path = Path()
        list.forEach {
            path.moveTo(it.posX, it.posY)
            gestureDescriptionBuild.addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0L,
                    it.clickInfo.pressTime
                )
            )
        }

        dispatchGesture(
            gestureDescriptionBuild.build(),
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
//                    LogUtils.d("自动点击完成 ")
                    _isCompleted = true
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    LogUtils.d("自动点击取消 ")
                    _isCompleted = true
                }
            },
            null
        )

    }


    private inner class BroadcastHandler(val context: Context) : BroadcastReceiver() {

        fun register() {
            context.registerReceiver(
                this,
                IntentFilter().apply {
                    addAction(BroadcastConstants.BROADCAST_ACTION_AUTO_CLICK)
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

//                        mainScope?.cancel()
                    }

                    BroadcastConstants.BROADCAST_ACTION_AUTO_CLICK -> {
                        when (getStringExtra(BroadcastConstants.KEY_ACTION)) {
                            ACTION_SHOW -> {
//                                mFloatingView.remove()
////                                mainScope?.cancel()
//                                mInterval = getLongExtra(BroadcastConstants.KEY_INTERVAL, 5000)
//                                mFloatingView.show()
                                menuManager.removeAll()
                                menuManager.showMenuManager()
                            }

                            ACTION_PLAY -> {
                                mPointX = getFloatExtra(BroadcastConstants.KEY_POINT_X, 0f)
                                mPointY = getFloatExtra(BroadcastConstants.KEY_POINT_Y, 0f)
//                                mainScope = MainScope()
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    autoClickView(mPointX, mPointY)
//                                }
                            }

                            ACTION_STOP -> {
//                                mainScope?.cancel()
                            }

                            ACTION_CLOSE -> {

//                                mainScope?.cancel()
                            }

                            else -> {
//                                Log.e(TAG, "action error")
                            }
                        }
                    }
                }
            }
        }
    }

}