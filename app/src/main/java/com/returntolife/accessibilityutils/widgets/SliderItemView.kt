package com.returntolife.accessibilityutils.widgets


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.returntolife.accessibilityutils.R
import com.returntolife.accessibilityutils.databinding.ViewFloatingClickBinding
import com.returntolife.accessibilityutils.tryUpdateView


/**
 *@author: hejiajun02@lizhi.fm
 *@date: 9/18/23
 *des:
 */

@SuppressLint("ViewConstructor")
class SliderItemView(
    context: Context, name:String
) :
    DragViewGroup(context) {



    private var binding: ViewFloatingClickBinding

    init {
        binding = ViewFloatingClickBinding.inflate(LayoutInflater.from(context), this)
        binding.root.setBackgroundResource(R.drawable.shape_bg_click)
        binding.tvName.text = name

    }


    fun setOnLongClickByCheck(l: OnLongClickListener?){
        setOnLongClickListener {
            if (isDragging.not()) {
                l?.onLongClick(it)?:false
            } else {
                false
            }
        }
    }

}