package com.returntolife.accessibilityutils

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
data class ClickInfo(val id:Int,var interval:Int,var firstDelayTime:Long = DEFAULT_FIRST_DELAY_TIME,var pressTime:Int = DEFAULT_PRESS_TIME ,var clickCount:Int=REVERT) {

 companion object{
     //0表示无限次
     const val REVERT = 0

     const val DEFAULT_PRESS_TIME = 50
     const val DEFAULT_FIRST_DELAY_TIME = 100L
 }
}