package com.returntolife.accessibilityutils

/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */
class ClickInfo(val id:Int,var interval:Int,var clickCount:Int=REVERT) {

 companion object{
     const val REVERT = 0
 }
}