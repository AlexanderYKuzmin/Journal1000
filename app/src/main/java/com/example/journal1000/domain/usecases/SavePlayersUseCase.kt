package com.example.journal1000.domain.usecases

import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.repository.GameDao

class SavePlayersUseCase(private val repository: GameDao) {
    suspend fun savePlayers(players: List<Player>) {
        repository.savePlayers(players)
    }
}