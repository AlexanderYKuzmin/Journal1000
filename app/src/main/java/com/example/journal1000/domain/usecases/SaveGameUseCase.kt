package com.example.journal1000.domain.usecases

import com.example.journal1000.domain.entity.Game
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.repository.GameDao

class SaveGameUseCase(private val repository: GameDao) {
    suspend fun saveGame(game: Game): Long {
        return repository.saveGame(game)
    }
}