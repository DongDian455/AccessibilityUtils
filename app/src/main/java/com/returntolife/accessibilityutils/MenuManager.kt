package com.returntolife.accessibilityutils

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import com.returntolife.accessibilityutils.databinding.DialogSelectedTypeBinding
import com.returntolife.accessibilityutils.databinding.ViewMenuBinding
import com.returntolife.accessibilityutils.widgets.FloatingClickView
import com.returntolife.accessibilityutils.widgets.SliderView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class MenuManager(
    private val context: Context,
    private val executionList: (ArrayList<AutoGestureInfo>) -> Unit
) {

    private var menuBinding: ViewMenuBinding

    private val gestureInfoList = ArrayList<AutoGestureInfo>()

    private var scope: CoroutineScope? = null

    @Volatile
    private var isPlaying = false


    init {
        menuBinding = ViewMenuBinding.inflate(LayoutInflater.from(context))


    }

    fun showMenuManager() {
        menuBinding = ViewMenuBinding.inflate(LayoutInflater.from(context))
        menuBinding.root.show(0, 0)

        initListener()
    }



    /**
     * cn: 根据类型移除对应的控件
     * [type] : 除了这个类型其他都移除
     */
    private fun removeExcludeType(type: GestureType) {
        val removeList = ArrayList<AutoGestureInfo>()

        gestureInfoList.forEach {
            if(it.timerManager.gestureTimerInfo.type!=type){
                removeList.add(it)
            }
        }

        removeList.forEach {
            it.gestureWidgetListener.removeWidget()
        }

        gestureInfoList.removeAll(removeList.toSet())
    }

    /**
     * cn : 选择脚本类型，点击和滑动暂不能同时进行
     * en : Select the script type, click and swipe can not be done at the same time
     */
    private fun showDialog() {
        val dialogBinding =
            DialogSelectedTypeBinding.inflate(LayoutInflater.from(context.applicationContext))

        val dialog = android.app.AlertDialog.Builder(context.applicationContext)
            .setView(dialogBinding.root)
            .create()


        dialogBinding.btnClick.setOnClickListener {
            removeExcludeType(GestureType.Click)

            val timerManager = TimerManager(
                GestureTimerInfo(
                    gestureInfoList.size + 1,
                    GestureType.Click
                )
            )
            FloatingClickView(timerManager
                , context
            ).apply {
                this.whenShowConfigDialog = {
                    stopTimerTask()
                }

                gestureInfoList.add(AutoGestureInfo(this,timerManager))
                this.showWidget()
            }

            dialog.dismiss()
        }

        dialogBinding.btnSlider.setOnClickListener {
            removeExcludeType(GestureType.Slider)

            val timerManager = TimerManager(GestureTimerInfo(1,GestureType.Slider, pressTime = 300))
            SliderView(context,timerManager).apply {
                this.whenShowConfigDialog = {
                    stopTimerTask()
                }
                gestureInfoList.add(AutoGestureInfo(this,timerManager))
                showWidget()
            }

            dialog.dismiss()
        }


        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        dialog.window?.setType(flag)
        dialog.show()


    }


    private fun initListener() {
        menuBinding.let {
            it.ivAdd.setOnClickListener {
                stopTimerTask()

                showDialog()

            }

            it.ivRemove.setOnClickListener {

                stopTimerTask()

                if(gestureInfoList.isNotEmpty()){
                    gestureInfoList[gestureInfoList.lastIndex]
                        .apply {
                            this.gestureWidgetListener.removeWidget()
                            gestureInfoList.remove(this)
                        }
                }
            }

            it.ivClose.setOnClickListener {
                stopAndRemoveAll()
            }

            it.ivPlay.setOnClickListener {
                if (isPlaying) {
                    stopTimerTask()
                } else {
                    startTimerTask()
                }
            }
        }
    }


    private fun stopTimerTask() {
        if (isPlaying) {
            isPlaying = false
            menuBinding.ivPlay.setImageResource(R.mipmap.ic_auto_click_play)
            scope?.cancel()

            gestureInfoList.forEach {
                it.timerManager.reset()
            }
        }
    }

    private fun startTimerTask() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        menuBinding.ivPlay.setImageResource(R.mipmap.ic_auto_click_pause)
        scope?.cancel()

        scope = MainScope().apply {
            launch(Dispatchers.IO) {
                loopCheck()
            }
        }
    }


    private suspend fun loopCheck() {
        //每隔16.6毫秒检测一次,即最多1秒点击60次

         val autoList = ArrayList<AutoGestureInfo>()
        gestureInfoList.forEach {
            if(it.timerManager.checkCanDispatchGesture()){
                autoList.add(it)
            }
        }

        if(autoList.isNotEmpty()){
            executionList.invoke(autoList)
        }

        delay(17)

        loopCheck()
    }

    fun stopAndRemoveAll() {
        stopTimerTask()

        gestureInfoList.forEach {
            it.gestureWidgetListener.removeWidget()
        }
        gestureInfoList.clear()
        menuBinding.root.remove()

    }


}