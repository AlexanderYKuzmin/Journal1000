package com.example.journal1000.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.journal1000.data.db.AppDatabase
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.usecases.DeleteGameWithScoresUseCase
import com.example.journal1000.domain.usecases.GetListOfGamesUseCase
import com.example.journal1000.extensions.toDate
import kotlinx.coroutines.launch
import java.util.*

class ListGamesViewModel(
    val application: Application)
    : ViewModel(){

    private val repository = AppDatabase.getInstance(application).gameDao()
    private lateinit var startDate: Date
    private lateinit var endDate: Date

    private var _games: MutableLiveData<MutableList<GameWithScores>> = MutableLiveData()
    val games: LiveData<MutableList<GameWithScores>>
        get() = _games

    private val deleteGameWithScoresUseCase = DeleteGameWithScoresUseCase(repository)
    private val getListOfGamesUseCase = GetListOfGamesUseCase(repository)

    fun loadGames(startDate: String, endDate: String) {
        this.startDate = startDate.toDate() ?: MIN_DATE
        this.endDate = endDate.toDate() ?: MAX_DATE
        viewModelScope.launch {
            _games.value = mutableListOf()
            _games.value = getListOfGamesUseCase.getListOfGames(
                this@ListGamesViewModel.startDate,
                this@ListGamesViewModel.endDate
            )
        }
    }

    fun deleteGame(gameWithScores: GameWithScores) {
        viewModelScope.launch {
            deleteGameWithScoresUseCase.deleteGameWithScores(listOf(gameWithScores.game))
        }
        val changedGames = games.value?.toMutableList()
        changedGames?.remove(gameWithScores)
        _games.value = changedGames!!
    }

    companion object {
        val MIN_DATE = Date(0)
        val MAX_DATE = Date(Long.MAX_VALUE)
    }
}