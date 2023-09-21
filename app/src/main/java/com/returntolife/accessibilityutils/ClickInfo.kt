package com.returntolife.accessibilityutils

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
data class ClickInfo(
    val id: Int,
    var interval: Long = DEFAULT_INTERVAL,
    var firstDelayTime: Long = DEFAULT_FIRST_DELAY_TIME,
    var pressTime: Long = DEFAULT_PRESS_TIME,
    var clickCount: Int = REVERT
) {

    companion object {
        //0表示无限次
        const val REVERT = 0

        const val DEFAULT_PRESS_TIME = 50L
        const val DEFAULT_FIRST_DELAY_TIME = 100L
        const val DEFAULT_INTERVAL = 1000L
    }
}

data class GestureInfo(val posX:Float,val posY:Float,val clickInfo: ClickInfo)