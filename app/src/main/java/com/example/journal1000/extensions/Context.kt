package com.example.journal1000.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

@SuppressLint("ResourceType")
fun Context.attrValue(@AttrRes attr: Int): Int {
    val tv = TypedValue()
    if (theme.resolveAttribute(attr, tv, true)) return tv.data
    else throw Resources.NotFoundException("Resource with id $attr not found")
}