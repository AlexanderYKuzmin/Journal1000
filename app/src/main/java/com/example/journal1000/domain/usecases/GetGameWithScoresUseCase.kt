package com.example.journal1000.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.repository.GameDao

class GetGameWithScoresUseCase(private val repository: GameDao) {

    suspend fun getGameWithScores(id: Long): GameWithScores? {
        return repository.getGameWithScores(id)
    }
}