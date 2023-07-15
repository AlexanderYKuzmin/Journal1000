package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.App
import com.example.journal1000.R
import com.example.journal1000.data.MessageHolder
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_ONE
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager

abstract class GameScoreListBaseFragment: Fragment() {

    protected abstract var viewModel: GameViewModel

    protected var savePointsListener: OnFragmentBehaviorControlManager? = null

    protected var isGameFinished = false
    protected var isBackStepVisible = false

    protected var delayAuctionDataAppearance = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            savePointsListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement listener!")
        }
    }

    val range = IntRange(0,8)
    var players: List<Player> = listOf()

    protected open fun renderHeader() {
        players = viewModel.players
        if (players.isEmpty()) return
    }

    protected fun setBackStepVisibility(isBackStepVisible: Boolean) = when {
            isBackStepVisible -> View.VISIBLE
            else -> View.INVISIBLE
    }

    protected fun setTextBtnSavePoints(isGameFinished: Boolean) = when {
        isGameFinished -> getString(R.string.finish_game)
        else -> getString(R.string.save_points)
    }

    protected fun checkAndSetLogo(tvNames: Array<TextView>) {
        for (i in players.indices) {
            tvNames[i].setCompoundDrawablesWithIntrinsicBounds(getLogo(players[i]), 0, 0, 0)
            tvNames[i].compoundDrawablePadding = 8
        }
    }

    private fun getLogo(player: Player) : Int {
        return if (player.onHundred) R.drawable.hundred_20 else R.drawable.tip_placeholder
    }

    protected fun checkAndSetBarrelsCountAndVisibility(
        ivBarrels: Array<ImageView>,
        tvBarrelsCount: Array<TextView>
    ) {
        for (i in players.indices) {
            if (players[i].isOnBarrel) {
                ivBarrels[i].visibility = View.VISIBLE
                tvBarrelsCount[i].text =
                    String.format(getString(R.string.barrels_count, players[i].onBarrelAttemptCount))
                tvBarrelsCount[i].visibility = View.VISIBLE
            } else {
                ivBarrels[i].visibility = View.GONE
                tvBarrelsCount[i].visibility = View.GONE
            }
        }
    }

    protected fun checkAndSetBoltsCountAndVisibility(
        ivBolts: Array<ImageView>,
        tvBoltsCount: Array<TextView>
    ) {
        for (i in players.indices) {
            if (players[i].boltNumber > 0) {
                ivBolts[i].visibility = View.VISIBLE
                tvBoltsCount[i].text =
                    String.format(getString(R.string.bolts_count, players[i].boltNumber))
                tvBoltsCount[i].visibility = View.VISIBLE
            } else {
                ivBolts[i].visibility = View.GONE
                tvBoltsCount[i].visibility = View.GONE
            }
        }
    }

    protected fun checkAndSetAuctionDataAndVisibility(
        tiLayouts: Array<View>,
        tvAuctionData: Array<AutoCompleteTextView>
    ) {
        for (i in players.indices) {
            if (players[i].requestedPoints >= 100) {
                tiLayouts[i].visibility = View.VISIBLE
                tiLayouts[i].alpha = 0.0f
                tiLayouts[i].animate().apply {
                    interpolator = LinearInterpolator()
                    duration = 500
                    alpha(1f)
                    startDelay = delayAuctionDataAppearance.toLong()
                    start()
                }
                tvAuctionData[i].setText(players[i].requestedPoints.toString())
            }
            else {
                tiLayouts[i].visibility = View.INVISIBLE
            }
        }
    }



    protected fun checkAndSetBarrelsVisibility(iv: View, tv: View, player: Player) {
        if (player.isOnBarrel) {
            iv.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
        }
        else {
            iv.visibility = View.GONE
            tv.visibility = View.GONE
        }
    }

    protected fun checkAndSetBoltsVisibility(iv: View, tv: View, player: Player) {
        if (player.boltNumber > 0) {
            iv.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
        }
        else {
            iv.visibility = View.GONE
            tv.visibility = View.GONE
        }
    }

    protected fun checkAndSetAuctionDataVisibility(til: View, player: Player) {
        if (player.requestedPoints >= 100) {
            til.visibility = View.VISIBLE
            til.alpha = 0.0f
            til.animate().apply {
                interpolator = LinearInterpolator()
                duration = 500
                alpha(1f)
                startDelay = delayAuctionDataAppearance.toLong()
                start()
            }
        }
        else {
            til.visibility = View.INVISIBLE
        }

    }

    protected fun setAuctionVisibility(isGameFinished: Boolean) = when {
        isGameFinished -> View.INVISIBLE
        /*players.any { it.isOnBarrel } -> View.INVISIBLE*/
        else -> View.VISIBLE
    }

    protected fun showMessages() {
        MessageHolder.show(MessageHolder.GAME_SCORE_LIST_DEST)
    }

    override fun onDestroy() {
        Log.d("GameScorelistFragment", "on Destroy")
        super.onDestroy()
    }

    companion object {
        const val GAME_SCORE_LIST = "gameList"

        @JvmStatic
        fun newInstance(gameType: GameType) =
            when(gameType) {
                GameType.THREE_PLAYER_GAME -> GameScoreList3PFragment()
                GameType.TWO_PLAYER_GAME -> GameScoreList2PFragment()
            }
    }
}