package com.example.journal1000.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.PlayerOrder
import com.example.journal1000.presentation.fragments.PointsEditionFragment
import java.lang.RuntimeException
import kotlin.math.round

class PointsEditionViewModel(application: Application) : AndroidViewModel(application) {

    private val _errorInputPoints = MutableLiveData(arrayOf(false, false, false))
    val errorInputPoints: LiveData<Array<Boolean>>
        get() = _errorInputPoints

    lateinit var pointsInt: List<Int>

    var mode: Int = 0


    fun resetErrorInputPoints(playerNumber: Int) {
        val errorList = errorInputPoints.value
        errorList!![playerNumber] = false
        _errorInputPoints.value = errorList
    }

    fun validateInput(playersPoints: List<String>): Boolean {
        var result = true
        val errorList = arrayOf(false, false, false)
        val regex = Regex("^$|^-?\\d+")

        for (i in playersPoints.indices) {
            if (!playersPoints[i].matches(regex)) { //|| playersPoints[i].isNullOrEmpty()
                errorList[i] = true
                result = false
            }
        }
        _errorInputPoints.value = errorList
        return result
    }

    fun validateInputValuesAndSetUp(playersPoints: List<String>, players: Array<Player>):Boolean {
        var result = true
        val errorList = arrayOf(false, false, false)
        pointsInt = playersPoints.map {
            if (it.isNullOrEmpty()) 0
            else it.toInt()
        }
        for (i in pointsInt.indices) {
            if (pointsInt[i] < 401 && pointsInt[i] > -401) {
                errorList[i] = if (mode == PointsEditionFragment.SAVE_POINTS_MODE) {
                    checkForNotAccordingRequest(pointsInt[i], players[i])
                } else checkForNegativeRequest(pointsInt[i])
            } else errorList[i] = true
        }
        _errorInputPoints.value = errorList
        result = !errorList.any { it }

        return result
    }

    fun getAuctionResult(): Pair<PlayerOrder, Int> {
        if (mode == PointsEditionFragment.AUCTION_MODE) {
            val max = pointsInt.maxOf { it }
            val playerOrder: PlayerOrder = PlayerOrder.fromInt(pointsInt.indexOf(max))
            val roundedResult = (round(max.toDouble() / 5) * 5).toInt()
            return Pair(playerOrder, roundedResult)
        }
        throw RuntimeException("Unexpected case")
    }

    private fun checkForNotAccordingRequest(points: Int, player: Player): Boolean {
        val reqPoints = player.requestedPoints
        return false
    }

    private fun checkForNegativeRequest(points: Int): Boolean {
        return points < 0
    }
}