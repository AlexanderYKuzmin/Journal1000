package com.example.journal1000.data

import android.view.Gravity
import android.widget.Toast
import com.example.journal1000.App
import kotlinx.coroutines.*

object MessageHolder {
    const val GAME_SCORE_LIST_DEST = 2
    const val DEST_GAMES = 0
    const val DEST_SETTINGS = 1

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var messagesGameScoreList: MutableList<String> = mutableListOf()
    private var messagesGames: MutableList<String> = mutableListOf()
    private var messagesSettings: MutableList<String> = mutableListOf()

    fun addMessage(destination: Int, message: String) {
        when(destination) {
            DEST_GAMES -> messagesGames.add(message)
            GAME_SCORE_LIST_DEST -> messagesGameScoreList.add(message)
            DEST_SETTINGS -> messagesSettings.add(message)
        }
    }

    fun clear(destination: Int) {
        when(destination) {
            DEST_GAMES -> messagesGames.clear()
            GAME_SCORE_LIST_DEST -> messagesGameScoreList.clear()
            DEST_SETTINGS -> messagesSettings.clear()
        }
    }

    fun getMessages(destination: Int): List<String> {
        return when(destination) {
            DEST_GAMES -> messagesGames.toList()
            GAME_SCORE_LIST_DEST -> messagesGameScoreList.toList()
            DEST_SETTINGS -> messagesSettings.toList()
            else -> throw RuntimeException("Wrong destination")
        }
    }

    fun show(destination: Int) {
        val messages = getMessages(destination)
        if (messages.isEmpty()) return
        coroutineScope.launch{
            for (message in messages) {
                val toast: Toast = Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
                delay(2100)
            }
            clear(destination)
        }
    }

    fun scopeCancel() {
        coroutineScope.cancel()
    }
}