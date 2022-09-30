package com.example.journal1000.domain.usecases

import com.example.journal1000.domain.entity.Game
import com.example.journal1000.domain.repository.GameDao

class DeleteGameWithScoresUseCase(private val repository: GameDao) {

    suspend fun deleteGameWithScores(games: List<Game>) {
        repository.deleteGame(games)
        //repository.saveScores(gameWithScores.scores)
    }
}