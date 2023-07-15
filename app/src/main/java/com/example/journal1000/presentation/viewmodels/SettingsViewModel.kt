package com.example.journal1000.presentation.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.journal1000.data.db.AppDatabase
import java.lang.RuntimeException

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private var _numberOfPlayers = MutableLiveData(3)
    val numberOfPlayers: LiveData<Int>
        get() = _numberOfPlayers

    private var _names = mutableListOf<String>()
    val names: List<String>
        get() = _names

    fun handleNumberOfPlayersSelection(number: Int) {
        _numberOfPlayers.value = number
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleAcceptingGameSettings(pNames: Array<String>) {
        _names = pNames.map {
            it.trim().replaceFirstChar(Char::titlecase)
        }.toMutableList()
    }
}