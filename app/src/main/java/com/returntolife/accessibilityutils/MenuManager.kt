package com.returntolife.accessibilityutils

import android.content.Context
import android.view.LayoutInflater
import com.returntolife.accessibilityutils.databinding.ViewMenuBinding
import com.returntolife.accessibilityutils.widgets.FloatingClickView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class MenuManager(
    private val context: Context,
    private val clickListener: (ArrayList<GestureInfo>) -> Unit
) {

    private var menuBinding: ViewMenuBinding

    private val floatingClickViewList = ArrayList<FloatingClickView>()

    private val gestureInfoList = ArrayList<GestureInfo>()

    private var scope: CoroutineScope? = null

    @Volatile
    private var isPlaying = false

    init {
        menuBinding = ViewMenuBinding.inflate(LayoutInflater.from(context))
    }

    fun showMenuManager() {
        menuBinding = ViewMenuBinding.inflate(LayoutInflater.from(context))
        menuBinding.root.show()

        initListener()
    }


    private fun initListener() {
        menuBinding.let {
            it.ivAdd.setOnClickListener {
                stopAutoClick()

                //Set the id based on the size of the list
                val clickInfo =
                    ClickInfo(floatingClickViewList.size + 1)

                FloatingClickView(context).apply {
                    this.clickInfo = clickInfo
                    this.whenShowConfigDialog = {
                        stopAutoClick()
                    }
                    floatingClickViewList.add(this)
                    this.show()
                }
            }

            it.ivRemove.setOnClickListener {
                stopAutoClick()

                if (floatingClickViewList.size > 0) {
                    val view = floatingClickViewList[floatingClickViewList.size - 1];
                    view.remove()
                    floatingClickViewList.remove(view)
                }
            }

            it.ivClose.setOnClickListener {
                removeAll()
            }

            it.ivPlay.setOnClickListener {
                if (isPlaying) {
                    stopAutoClick()
                } else {
                    startAutoClick()
                }
            }
        }
    }


    private fun stopAutoClick() {
        if (isPlaying) {
            isPlaying = false
            menuBinding.ivPlay.setImageResource(R.mipmap.ic_auto_click_play)
            scope?.cancel()
            floatingClickViewList.forEach {
                it.reset()
            }
        }
    }

    private fun startAutoClick() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        menuBinding.ivPlay.setImageResource(R.mipmap.ic_auto_click_pause)
        scope?.cancel()

        scope = MainScope()

        scope!!.launch(Dispatchers.IO) {
            loopCheck()
        }


    }

    private suspend fun loopCheck() {
        //每隔16.6毫秒检测一次,即最多1秒点击60次

        gestureInfoList.clear()
        floatingClickViewList.forEach { view ->

            if (view.checkCanClick()) {
                val posArray = view.getViewPos()

                view.clickInfo?.let {
                    gestureInfoList.add(
                        GestureInfo(
                            if (posArray[0] > 0) (posArray[0] - 1).toFloat() else 0f,
                            if (posArray[1] > 0) (posArray[1] - 1).toFloat() else 0f,
                            it
                        )
                    )
                }

            }
        }

        if (gestureInfoList.size > 0) {
            val clickList = ArrayList(gestureInfoList)
            withContext(Dispatchers.Main) {
                clickListener.invoke(clickList)
            }
        }

        delay(17)

        loopCheck()
    }

    fun removeAll() {
        stopAutoClick()
        floatingClickViewList.forEach { view ->
            view.remove()
        }
        floatingClickViewList.clear()
        menuBinding.root.remove()

    }



}