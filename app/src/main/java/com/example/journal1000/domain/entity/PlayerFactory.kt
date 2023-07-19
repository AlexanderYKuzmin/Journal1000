package com.example.journal1000.domain.entity

class PlayerFactory {
    companion object {
        fun getNewPlayerInstance(name: String, ordinalNum: Int): Player {
            return Player(
                name = name,
                playerOrder = PlayerOrder.values()[ordinalNum],
                requestedPoints = if (ordinalNum == 0) 100 else 0
            )
        }
    }
}