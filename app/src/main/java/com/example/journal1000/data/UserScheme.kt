package com.example.journal1000.data

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserScheme {
    val GAME_ID = longPreferencesKey("id")
    val NUM_OF_PLAYERS = intPreferencesKey("number_of_players")
    val NAME_ONE = stringPreferencesKey("player_one")
    val NAME_TWO = stringPreferencesKey("player_two")
    val NAME_THREE = stringPreferencesKey("player_three")
}