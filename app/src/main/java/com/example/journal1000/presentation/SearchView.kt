package com.example.journal1000.presentation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.example.journal1000.R
import com.example.journal1000.databinding.LayoutSearchBinding
import com.example.journal1000.extensions.dpToPx
import kotlin.math.hypot

class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: LayoutSearchBinding = LayoutSearchBinding.inflate(LayoutInflater.from(context), this, true)
    var isOpen = false
    private val centerX: Double = 200.0
    private val centerY: Double = 0.0

    fun open() {
        if (isOpen) return
        isOpen = true
        animatedShow()
    }

    fun close() {
        if(!isOpen) return
        isOpen = false
        animatedHide()
    }

    private fun animatedShow() {
        visibility = View.VISIBLE
        val anim = TranslateAnimation(
            0f,
            0f,
            0f,
            200f
        ).apply {
            fillAfter = true
            duration = 2000L
        }
        val endRadius = hypot(centerX.toDouble(), height.toDouble())
        anim.start()
    }

    private fun animatedHide() {
        visibility = View.GONE
    }
}