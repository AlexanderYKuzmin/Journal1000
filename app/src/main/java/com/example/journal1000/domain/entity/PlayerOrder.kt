package com.example.journal1000.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PlayerOrder(val value: Int) : Parcelable {
    ONE(0),
    TWO(1),
    THREE(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}