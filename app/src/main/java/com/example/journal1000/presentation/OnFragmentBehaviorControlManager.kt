package com.example.journal1000.presentation

import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.PlayerOrder

interface OnFragmentBehaviorControlManager {
    fun onFinishSettingsEdition(playersNames: List<String>)

    fun onFinishSavePointsEdition(points: List<Int>)

    fun onFinishAuctionEdition(auctionData: Pair<PlayerOrder, Int>)

    fun onStartSavePointsEdition(mode: Int)

    fun onShowGameResult()

    fun onFinishGame()

    fun backToGameScoreList()

    fun onGameSelected(gameWithScores: GameWithScores)

}