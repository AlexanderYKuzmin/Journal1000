package com.example.journal1000.data

import com.example.journal1000.presentation.adapters.GameListItem

class DataHolder {
    companion object {
        fun getDefaultGameList(): List<GameListItem> {
            val list = mutableListOf<GameListItem>()
            val p1CountDefault = 100
            val p2CountDefault = 110
            val p3CountDefault = 120

            for (i in 0..20) {
                list.add(
                    GameListItem(
                        100 + i * 10,
                        123 + i * 10,
                        134 + i * 10,
                        i * 10,
                        i * 10,
                        i * 10
                    )
                )
            }
            return list
        }
    }
}