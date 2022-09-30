package com.example.journal1000.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameType(val value: Int) : Parcelable {
    TWO_PLAYER_GAME(2),
    THREE_PLAYER_GAME(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}