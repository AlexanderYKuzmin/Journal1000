package com.example.journal1000.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.R
import com.example.journal1000.databinding.FragmentGameScoreList3PBinding
import com.example.journal1000.extensions.substringIfOutOfRange
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_ONE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_THREE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_TWO
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.adapters.GameListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameScoreList3PFragment : GameScoreListBaseFragment() {

    private lateinit var binding: FragmentGameScoreList3PBinding

    override lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScoreList3PBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding.viewModel = viewModel

        val adapter = GameListAdapter(requireContext())
        val rvGameList: RecyclerView = binding.rvScore
        rvGameList.adapter = adapter

        setBtnListeners()
        renderHeader()
        showMessages()

        viewModel.gameList.observe(viewLifecycleOwner) {
            adapter.gameList = it
            rvGameList.scrollToPosition(it.size - 1)
            renderHeader()
        }
        viewModel.isBackStepPressed.observe(viewLifecycleOwner) {
            isBackStepVisible = !it
            renderBottomBar(isBackStepVisible = isBackStepVisible)
        }
        viewModel.isGameFinished.observe(viewLifecycleOwner) {
            isGameFinished = it
            renderBottomBar(isGameFinished = isGameFinished)
        }
    }

    private fun setBtnListeners() {
        binding.tvSavePoints.setOnClickListener {
            delayAuctionDataAppearance = 2000
            if (isGameFinished) savePointsListener?.onShowGameResult()
            else savePointsListener?.onStartSavePointsEdition(PointsEditionFragment.SAVE_POINTS_MODE)
        }
        binding.tvBackStep.setOnClickListener {
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
            checkAndSetLogo(arrayOf(tvP1Name, tvP2Name, tvP3Name))
            checkAndSetBarrelsCountAndVisibility(
                arrayOf(ivP1Barrel, ivP2Barrel, ivP3Barrel),
                arrayOf(tvP1BarrelCount, tvP2BarrelCount, tvP3BarrelCount)
            )
            checkAndSetBoltsCountAndVisibility(
                arrayOf(ivP1Bolt, ivP2Bolt, ivP3Bolt),
                arrayOf(tvP1BoltCount, tvP2BoltCount, tvP3BoltCount)
            )
            checkAndSetAuctionDataAndVisibility(
                arrayOf(tilP1Auction, tilP2Auction, tilP3Auction),
                arrayOf(tvP1Auction, tvP2Auction, tvP3Auction)
            )
        }
    }

    private fun renderBottomBar(
        isBackStepVisible: Boolean = this.isBackStepVisible,
        isGameFinished: Boolean = false
    ) {
        binding.tvBackStep.visibility = setBackStepVisibility(isBackStepVisible)
        binding.tvSavePoints.text = setTextBtnSavePoints(isGameFinished)
        binding.tvAuction.visibility = setAuctionVisibility(isGameFinished)
    }
}