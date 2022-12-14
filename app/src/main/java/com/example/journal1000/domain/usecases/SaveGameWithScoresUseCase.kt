package com.example.journal1000.domain.usecases

import android.util.Log
import com.example.journal1000.domain.entity.Game
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.Score
import com.example.journal1000.domain.repository.GameDao

class SaveGameWithScoresUseCase(private val repository: GameDao) {

    suspend fun saveGameWithScores(game: Game, scores: List<Score>, players: List<Player>): Long {
        val gameId = repository.saveGame(game)
        Log.d("SaveGameUseCase", "Game id = $gameId")

        scores.map { it.gameId = gameId }
        repository.saveScores(scores)

        players.map { it.gameId = gameId }
        repository.savePlayers(players)

        return gameId
    }
}