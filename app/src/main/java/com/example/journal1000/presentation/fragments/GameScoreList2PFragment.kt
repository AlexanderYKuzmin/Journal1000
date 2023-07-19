package com.example.journal1000.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.databinding.FragmentGameScoreList2PBinding
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.adapters.GameList2PAdapter


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
                arrayOf(ivP1Bolt2, ivP2Bolt2),
                arrayOf(tvP1BoltCount2, tvP2BoltCount2)
            )
            checkAndSetAuctionDataAndVisibility(
                arrayOf(tilP1Auction2, tilP2Auction2),
                arrayOf(tvP1Auction2, tvP2Auction2)
            )
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
        renderHeader()
    }
}