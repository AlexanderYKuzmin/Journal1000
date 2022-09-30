package com.example.journal1000.domain.usecases

import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.repository.GameDao
import java.util.Date

class GetListOfGamesUseCase(private val repository: GameDao) {

    /*fun getListOfGame(): List<GameWithScores> {
        return repository.getListOfGames()
    }*/

    suspend fun getListOfGames(fromDate: Date, toDate: Date): MutableList<GameWithScores> {
        return repository.getListOfGamesByDate(fromDate, toDate)
    }
}