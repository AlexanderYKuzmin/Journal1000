package com.example.journal1000.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.journal1000.data.UserScheme.GAME_ID
import com.example.journal1000.data.UserScheme.NAME_ONE
import com.example.journal1000.data.UserScheme.NAME_THREE
import com.example.journal1000.data.UserScheme.NAME_TWO
import com.example.journal1000.data.UserScheme.NUM_OF_PLAYERS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PrefManager(context: Context) {

    val dataStore = context.dataStore

    suspend fun writeData(
        id: Long,
        numberOfPlayers: Int,
        name1: String,
        name2: String,
        name3: String = "Noname"
    ) {

        Log.d("PrefManager", "write data id = $id")
        dataStore.edit { prefs ->
            prefs[GAME_ID] = id
            prefs[NUM_OF_PLAYERS] = numberOfPlayers
            prefs[NAME_ONE] = name1
            prefs[NAME_TWO] = name2
            prefs[NAME_THREE] = name3
            Log.d("PrefManager", "writing data pref[GAME_ID] = ${prefs[GAME_ID]}")
        }
    }

     suspend fun readData(
         keyId: Preferences.Key<Long>,
         keyNumberOfPlayers: Preferences.Key<Int>,
         keyName1: Preferences.Key<String>,
         keyName2: Preferences.Key<String>,
         keyName3: Preferences.Key<String> = NAME_THREE,
     ): GamePreferences {
         val flowValue = dataStore.data.map { prefs ->
             val id = prefs[keyId] ?: -1
             val numberOfPlayers = prefs[keyNumberOfPlayers] ?: -1
             val name1 = prefs[keyName1]
             val name2 = prefs[keyName2]
             val name3 = prefs[keyName3]

             GamePreferences(id, numberOfPlayers, name1, name2, name3)
         }
        return runBlocking(Dispatchers.IO) { flowValue.first() }
    }
}