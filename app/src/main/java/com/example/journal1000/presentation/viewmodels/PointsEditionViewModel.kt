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
            Log.d("PointsEdition validate input", "points[$i] = ${playersPoints[i]}")
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
            if (it.isNullOrEmpty()) {
                Log.d("VM Points edition", "String = $it")
                0
            } else {
                it.toInt()
            }
        }
        Log.d("VM Points edition", "PointsInt = ${pointsInt.forEach { Log.d("VM Points Edition", "pointsInt = $it") }}")
        for (i in pointsInt.indices) {
            if (pointsInt[i] < 401 && pointsInt[i] > -401) {
                errorList[i] = if (mode == PointsEditionFragment.SAVE_POINTS_MODE) {
                    checkForNotAccordingRequest(pointsInt[i], players[i])
                } else checkForNegativeRequest(pointsInt[i])
            } else errorList[i] = true
        }
        _errorInputPoints.value = errorList
        result = !errorList.any { it }
        Log.d("VM Points Edition", "errorList = ${errorList[0]}, ${errorList[1]}")
        Log.d("VM Points Edition", "Result = $result")
        return result
    }

    fun getAuctionResult(): Pair<PlayerOrder, Int> {
        Log.d("VM Points Edition", "get Auction Result")
        if (mode == PointsEditionFragment.AUCTION_MODE) {
            val max = pointsInt.maxOf { it }
            val playerOrder: PlayerOrder = PlayerOrder.fromInt(pointsInt.indexOf(max))
            Log.d("VM Points Edition getAuctionResult()", "max = $max, plasyerOrder = $playerOrder")
            val roundedResult = (round(max.toDouble() / 5) * 5).toInt()
            return Pair(playerOrder, roundedResult)
        }
        throw RuntimeException("Unexpected case")
    }

    private fun checkForNotAccordingRequest(points: Int, player: Player): Boolean {
        val reqPoints = player.requestedPoints
        if (reqPoints > 0) {
            if (points != reqPoints && points != -reqPoints && !player.isOnBarrel) return true
            else if (player.isOnBarrel && points < 0) return true
        }
        return false
    }

    private fun checkForNegativeRequest(points: Int): Boolean {
        return points < 0
    }
}