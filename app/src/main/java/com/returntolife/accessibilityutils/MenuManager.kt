package com.returntolife.accessibilityutils

import android.content.Context
import android.view.LayoutInflater
import com.returntolife.accessibilityutils.databinding.ViewMenuBinding

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class MenuManager(private val context: Context) {

    private var menuBinding: ViewMenuBinding? = null

    private val clickInfoList = ArrayList<FloatingClickView>()


    @Volatile
    private var isPlaying = false

    var clickListener: ((Float, Float, ClickInfo) -> Unit)? = null


    fun showMenuManager() {
        menuBinding = ViewMenuBinding.inflate(LayoutInflater.from(context))
        menuBinding!!.root.show()

        initListener()
    }


    private fun initListener() {
        menuBinding?.let {
            it.ivAdd.setOnClickListener {

                val clickInfo =
                    ClickInfo(clickInfoList.size + 1, 1000)

                FloatingClickView(context, clickInfo).apply {
                    clickInfoList.add(this)
                    this.show()
                }
            }

            it.ivRemove.setOnClickListener {
                if (clickInfoList.size > 0) {
                    val view = clickInfoList[clickInfoList.size - 1];
                    view.remove()
                    clickInfoList.remove(view)
                }
            }

            it.ivClose.setOnClickListener {
                remove()
            }

            it.ivPlay.setOnClickListener {
                if (isPlaying) {
                    isPlaying = false
                    menuBinding?.ivPlay?.setImageResource(R.mipmap.ic_play)
                    clickInfoList.forEach { view ->
                        view.stopAutoClick()
                    }
                } else {
                    isPlaying = true
                    menuBinding?.ivPlay?.setImageResource(R.mipmap.ic_pause)

                    clickInfoList.forEach { view ->
                        view.startAutoClick()
                    }
                }
            }
        }
    }


    fun remove() {
        clickInfoList.forEach {
            it.stopAutoClick()
        }
        isPlaying = false
        menuBinding?.root?.remove()

    }


}