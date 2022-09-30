package com.example.journal1000.presentation

import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.journal1000.R
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.extensions.substringIfOutOfRange

val range = IntRange(0,8)

@BindingAdapter("adjustName")
fun adjustName(textView: TextView, name: String) {
    val adjustName = name.trim().substringIfOutOfRange(range)
    textView.text = adjustName
}

/*
@BindingAdapter("boltBarrelCount")
fun bindingBoltCount(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.bolts_count),
        count
    )
}

@BindingAdapter("logo")
fun chooseLogoHundred(textView: TextView, onHundred: Boolean) {
    textView.setCompoundDrawablesWithIntrinsicBounds(getLogo(onHundred), 0, 0, 0)
    textView.compoundDrawablePadding = 8
}

@BindingAdapter("requestPointsCount")
fun requestPoints(editable: AutoCompleteTextView, requestedPoints: Int) {
    editable.setText(requestedPoints.toString())
}

@BindingAdapter("requestedPoints", "delayAuctionDataAppearance")
fun checkAndSetAuctionDataVisibility(view: View, requestedPoints: Int, delayAuctionDataAppearance: Int) {
    if (requestedPoints >= 100) {
        view.visibility = View.VISIBLE
        view.alpha = 0.0f
        view.animate().apply {
            interpolator = LinearInterpolator()
            duration = 500
            alpha(1f)
            startDelay = delayAuctionDataAppearance.toLong()
            start()
        }
    }
    else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("barrelVisibility")
fun checkAndSetBarrelVisibility(view: View, isOnBarrel: Boolean) {
    if (isOnBarrel) {
        view.visibility = View.VISIBLE
        view.visibility = View.VISIBLE
    }
    else {
        view.visibility = View.GONE
        view.visibility = View.GONE
    }
}

@BindingAdapter("boltVisibility")
fun checkAndSetBoltVisibility(view: View, boltNumber: Int) {
    if (boltNumber > 0) {
        view.visibility = View.VISIBLE
        view.visibility = View.VISIBLE
    }
    else {
        view.visibility = View.GONE
        view.visibility = View.GONE
    }
}

private fun getLogo(onHundred: Boolean) : Int {
    return if (onHundred) R.drawable.hundred_20 else R.drawable.tip_placeholder
}*/
