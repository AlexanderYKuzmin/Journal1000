package com.example.journal1000.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.R
import com.example.journal1000.databinding.FragmentGameScoreList2PBinding
import com.example.journal1000.extensions.substringIfOutOfRange
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_ONE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_TWO
import com.example.journal1000.presentation.adapters.GameList2PAdapter
import kotlinx.coroutines.runBlocking


class GameScoreList2PFragment : GameScoreListBaseFragment() {

    private lateinit var binding: FragmentGameScoreList2PBinding

    override lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScoreList2PBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding.viewModel = viewModel

        val adapter = GameList2PAdapter(requireContext())
        val rvGameList2: RecyclerView = binding.rvScore2P
        rvGameList2.adapter = adapter

        setBtnListeners()
        renderHeader()
        showMessages()

        viewModel.gameList.observe(viewLifecycleOwner) {
            adapter.gameList = it
            rvGameList2.scrollToPosition(it.size - 1)
            renderHeader()
        }
        viewModel.isBackStepPressed.observe(viewLifecycleOwner) {
            isBackStepVisible = !it
            renderBottomBar(isBackStepVisible = isBackStepVisible)
            renderHeader()
        }
        viewModel.isGameFinished.observe(viewLifecycleOwner) {
            Log.d("GameScoreList2PFragment", "FINISH GAME = $it")
            isGameFinished = it
            renderBottomBar(isGameFinished = isGameFinished)
        }
    }

    private fun setBtnListeners() {
        binding.tvSavePoints2P.setOnClickListener {
            delayAuctionDataAppearance = 2000
            if (isGameFinished) savePointsListener?.onShowGameResult()
            else savePointsListener?.onStartSavePointsEdition(PointsEditionFragment.SAVE_POINTS_MODE)
        }
        binding.tvBackStep2P.setOnClickListener {
            viewModel.handleCancelCount()
        }
        binding.tvAuction.setOnClickListener {
            delayAuctionDataAppearance = 0
            savePointsListener?.onStartSavePointsEdition(PointsEditionFragment.AUCTION_MODE)
        }
    }

    override fun renderHeader() {
        super.renderHeader()
        with(binding) {

            checkAndSetLogo(arrayOf(tvP1Name2, tvP2Name2))
            checkAndSetBarrelsCountAndVisibility(
                arrayOf(ivP1Barrel2, ivP2Barrel2),
                arrayOf(tvP1BarrelCount2, tvP2BarrelCount2)
            )
            checkAndSetBoltsCountAndVisibility(
                arrayOf(ivBolt2, ivP2Bolt2),
                arrayOf(tvP1BoltCount2, tvP2BoltCount2)
            )

            checkAndSetAuctionDataAndVisibility(
                arrayOf(tilP1Auction2, tilP2Auction2),
                arrayOf(tvP1Auction2, tvP2Auction2)
            )


            //checkAndSetBarrelsVisibility(ivP1Barrel2, tvP1BarrelCount2, players[PLAYER_ONE])
            //checkAndSetBoltsVisibility(ivBolt2, tvP1BoltCount2, players[PLAYER_ONE])

            //setNames(arrayOf(tvP1Name2, tvP2Name2))
            //tvP1Name2.text = players[PLAYER_ONE].name.substringIfOutOfRange(range)
            //tvP1BarrelCount2.text =
             //   String.format(
              //      getString(
                //        R.string.barrels_count,
                  //      players[PLAYER_ONE].onBarrelAttemptCount
                //    )
               // )
            //tvP1BoltCount2.text =
                //String.format(getString(R.string.bolts_count, players[PLAYER_ONE].boltNumber))
            //tvP1Name2.setCompoundDrawablesWithIntrinsicBounds(getLogo(players[PLAYER_ONE]), 0, 0, 0)
            //tvP1Name2.compoundDrawablePadding = 8
            //tvP1Auction2.setText(players[PLAYER_ONE].requestedPoints.toString())

            //checkAndSetBarrelsVisibility(ivP2Barrel2, tvP2BarrelCount2, players[PLAYER_TWO])
            //checkAndSetBoltsVisibility(ivP2Bolt2, tvP2BoltCount2, players[PLAYER_TWO])
            //tvP2Name2.text = players[PLAYER_TWO].name.substringIfOutOfRange(range)
            /*tvP2BarrelCount2.text =
                String.format(
                    getString(
                        R.string.barrels_count,
                        players[PLAYER_TWO].onBarrelAttemptCount
                    )
                )
            tvP2BoltCount2.text =
                String.format(getString(R.string.bolts_count, players[PLAYER_TWO].boltNumber))*/
            //tvP2Name2.setCompoundDrawablesWithIntrinsicBounds(getLogo(players[PLAYER_TWO]), 0, 0, 0)
            //tvP2Name2.compoundDrawablePadding = 8
            //tvP2Auction2.setText(players[PLAYER_TWO].requestedPoints.toString())

            /*runBlocking {
                checkAndSetAuctionDataVisibility(tilP1Auction2, players[PLAYER_ONE])
                checkAndSetAuctionDataVisibility(tilP2Auction2, players[PLAYER_TWO])
            }*/
        }
    }

    private fun renderBottomBar(
        isBackStepVisible: Boolean = this.isBackStepVisible,
        isGameFinished: Boolean = false
    ) {
        binding.tvBackStep2P.visibility = setBackStepVisibility(isBackStepVisible)
        binding.tvSavePoints2P.text = setTextBtnSavePoints(isGameFinished)
        binding.tvAuction.visibility = setAuctionVisibility(isGameFinished)
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAmeScoreList2P", "on resume")
        renderHeader()
    }
}