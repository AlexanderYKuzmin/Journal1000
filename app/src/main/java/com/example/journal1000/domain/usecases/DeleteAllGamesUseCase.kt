package com.example.journal1000.domain.usecases

import com.example.journal1000.domain.repository.GameDao

class DeleteAllGamesUseCase(private val repository: GameDao) {

    fun deleteAll() {
        repository.deleteAll()
    }
}