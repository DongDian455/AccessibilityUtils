package com.returntolife.accessibilityutils


import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.LogUtils
import com.returntolife.accessibilityutils.databinding.DialogMotifyClickinfoBinding
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
class FloatingClickView(private val mContext: Context, val clickInfo: ClickInfo) : DragViewGroup(mContext) {


    private var scope: CoroutineScope? = null

    var clickListener:((Float,Float,ClickInfo)->Unit)?=null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_floating_click, this)

        initListener()
    }

    private var timeTemp = 0L
    private var clickCount = 1

    private suspend fun checkClick(){
        delay(100)

        if(System.currentTimeMillis()-timeTemp>clickInfo.interval){
            val intArray = getViewPos()
            clickListener?.invoke(intArray[0].toFloat(),intArray[1].toFloat(),clickInfo)
            LogUtils.d("点击 id=${clickInfo.id} count =${clickCount++}")
            timeTemp = System.currentTimeMillis()
        }

        checkClick()
    }

    fun startAutoClick(){
        scope?.cancel()
        scope = MainScope()
        scope?.launch(Dispatchers.IO) {
            checkClick()
        }
    }

    fun stopAutoClick(){
        scope?.cancel()
    }


    override fun remove() {
        scope?.cancel()
        super.remove()

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        setOnLongClickListener {
//
            showDialog()
            true
        }
//
//        mView.setOnClickListener {
//
//            val location = IntArray(2)
//            it.getLocationOnScreen(location)
//
//            val intent = Intent().apply {
//                action = BroadcastConstants.BROADCAST_ACTION_AUTO_CLICK
//                when (mCurrentState) {
//                    STATE_NORMAL -> {
//                        mCurrentState = STATE_CLICKING
//                        putExtra(BroadcastConstants.KEY_ACTION, AutoClickService.ACTION_PLAY)
//                        putExtra(BroadcastConstants.KEY_POINT_X, (location[0] - 1).toFloat())
//                        putExtra(BroadcastConstants.KEY_POINT_Y, (location[1] - 1).toFloat())
////                        ivIcon?.setImageResource(R.drawable.ic_auto_click_icon_green_24)
//                    }
//                    STATE_CLICKING -> {
//                        mCurrentState = STATE_NORMAL
//                        putExtra(BroadcastConstants.KEY_ACTION, AutoClickService.ACTION_STOP)
////                        ivIcon?.setImageResource(R.drawable.ic_auto_click_icon_gray_24)
//                    }
//                }
//            }
//
//            context.sendBroadcast(intent)
//        }
    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(mContext)
        val dialog = builder.create()

        val dialogBinding = DialogMotifyClickinfoBinding.inflate(LayoutInflater.from(mContext))
        //设置对话框布局
        dialog.setView(dialogBinding.root)
        dialog.show()

        dialogBinding.etInterval.setText(clickInfo.interval)
        dialogBinding.etCount.setText(clickInfo.clickCount)

        dialogBinding.btnOk.setOnClickListener(OnClickListener {
            clickInfo.interval = dialogBinding.etInterval.text.toString().toInt()
            clickInfo.clickCount = dialogBinding.etCount.text.toString().toInt()

            dialog.dismiss()
        })

    }


//    fun getViewPos():IntArray{
//        val location = IntArray(2)
//        getLocationOnScreen(location)
//        return location
//    }
//
//    fun show() {
//        mParams = WindowManager.LayoutParams()
//        mParams?.apply {
//            gravity = Gravity.CENTER
//            //总是出现在应用程序窗口之上
//            type = if (Build.VERSION.SDK_INT >= 26) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
//            }
//            //设置图片格式，效果为背景透明
//            format = PixelFormat.RGBA_8888
//
//            flags =
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
//                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//
//            width = LayoutParams.WRAP_CONTENT
//            height = LayoutParams.WRAP_CONTENT
//            if (mView.isAttachedToWindow) {
//                mWindowManager.tryRemoveView(mView)
//            }
//            mWindowManager.tryAddView(mView, this)
//        }
//    }
//
//    fun remove() {
//        mCurrentState = STATE_NORMAL
////        ivIcon?.setImageResource(R.drawable.ic_auto_click_icon_gray_24)
//        mWindowManager.tryRemoveView(mView)
//    }

}