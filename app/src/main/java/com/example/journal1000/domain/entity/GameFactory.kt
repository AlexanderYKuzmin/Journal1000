package com.example.journal1000.domain.entity

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.journal1000.domain.entity.GameType.THREE_PLAYER_GAME
import com.example.journal1000.domain.entity.GameType.TWO_PLAYER_GAME
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_ONE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_THREE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_TWO
import com.example.journal1000.presentation.adapters.GameListItem
import java.lang.RuntimeException
import java.util.*


class GameFactory(private val vm: GameViewModel) {

    fun getNewGameInstance(numberOfPlayers: Int): Game {
        return Game(
            numberOfPlayers = numberOfPlayers,
            gameDate = Date(),
            onHundredPlayer = PlayerOrder.ONE,
            type = GameType.fromInt(numberOfPlayers),
        )
    }

    fun getNewGameWithScoresInstance(): GameWithScores {
        return GameWithScores(
            game = vm.game.copy(),
            players = vm.players
        )
    }

    fun getGameWithScoresInstance(): GameWithScores {
        return GameWithScores(
            game = vm.game.copy(),
            scores = vm.scores,
            players = vm.players
        )
    }

    fun getScoreInstance(points: List<Int>): Score {
        val game = vm.game
        val scores = vm.scores
        //val lastScore = if (scores.isEmpty()) Score() else scores.last()

        return Score(
            playerOneCount = points[0],
            playerTwoCount = points[1],
            playerThreeCount = if (game.numberOfPlayers == 3) points[2] else 0,
            gameId = game.gameId ?: throw RuntimeException("There is no Game to store Score"),
            step = scores.size + 1
        )
    }

    fun createGameListItem(points: List<Int>, playersCounts: List<Int>): GameListItem {
        return when (vm.gameType) {
            THREE_PLAYER_GAME -> GameListItem(
                p1Count = playersCounts[PLAYER_ONE],
                p2Count = playersCounts[PLAYER_TWO],
                p3Count = playersCounts[PLAYER_THREE],
                p1CountCurrent = points[PLAYER_ONE],
                p2CountCurrent = points[PLAYER_TWO],
                p3CountCurrent = points[PLAYER_THREE]
            )
            TWO_PLAYER_GAME -> GameListItem(
                p1Count = playersCounts[PLAYER_ONE],
                p2Count = playersCounts[PLAYER_TWO],
                p1CountCurrent = points[PLAYER_ONE],
                p2CountCurrent = points[PLAYER_TWO]
            )
        }
    }


}