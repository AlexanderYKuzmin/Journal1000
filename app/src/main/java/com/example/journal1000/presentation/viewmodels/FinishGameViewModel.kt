package com.example.journal1000.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.GameType.*
import com.example.journal1000.domain.entity.Player

class FinishGameViewModel(val players: Array<Player>, val gameType: GameType) : ViewModel() {

    private lateinit var gameResult: List<Pair<String, Int>>

    fun getResult(): List<Pair<String, Int>> {
        players.sortBy { player -> player.count }

        when (gameType) {
            THREE_PLAYER_GAME -> {
                val resultHigh = (1000 - players[1].count) * 2
                var resultMiddle = (players[1].count - 1000) + (players[1].count - players[0].count)
                var resultLow = players[0].count - 1000

                if (players[1].count == players[0].count) {
                    resultMiddle = players[1].count - 1000
                    resultLow = resultMiddle
                }

                gameResult = listOf(
                    Pair(players[2].name, resultHigh),
                    Pair(players[1].name, resultMiddle),
                    Pair(players[0].name, resultLow)
                )
            }
            TWO_PLAYER_GAME -> {
                val resultHigh = 1000 - players[0].count
                val resultLow = players[0].count - 1000

                gameResult = listOf(
                    Pair(players[1].name, resultHigh),
                    Pair(players[0].name, resultLow)
                )
            }
        }
        return gameResult
    }
}
