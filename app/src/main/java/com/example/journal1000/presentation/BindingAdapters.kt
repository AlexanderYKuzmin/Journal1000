package com.example.journal1000.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.journal1000.extensions.substringIfOutOfRange

val range = IntRange(0,8)

@BindingAdapter("adjustName")
fun adjustName(textView: TextView, name: String) {
    val adjustName = name.trim().substringIfOutOfRange(range)
    textView.text = adjustName
}

