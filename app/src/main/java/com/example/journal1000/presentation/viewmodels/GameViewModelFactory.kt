package com.example.journal1000.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.Player


class GameViewModelFactory(
    private val application: Application,
    private val players: Array<Player> = emptyArray(),
    private val gameType: GameType = GameType.THREE_PLAYER_GAME,
    private val mode: Int = 0
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishGameViewModel::class.java)) {
            return FinishGameViewModel(players, gameType) as T
        } else if (modelClass.isAssignableFrom(ListGamesViewModel::class.java)) {
            return ListGamesViewModel(application) as T
        } else if (modelClass.isAssignableFrom(PointsEditionViewModel::class.java)) {
            return PointsEditionViewModel(application) as T
        }
        throw RuntimeException("Unknown view model class $modelClass")
    }
}