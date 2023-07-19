package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.R
import com.example.journal1000.databinding.FragmentFinishGameBinding
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.viewmodels.FinishGameViewModel
import com.example.journal1000.presentation.viewmodels.GameViewModelFactory

private const val PLAYERS = "players"
private const val GAME_TYPE = "game_type"


class FinishGameFragment : Fragment() {
    private lateinit var players: Array<Player>
    private lateinit var gameType: GameType

    private lateinit var binding: FragmentFinishGameBinding
    private lateinit var viewModel: FinishGameViewModel
    private lateinit var viewModelFactory: GameViewModelFactory

    private var finishGameListener: OnFragmentBehaviorControlManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameType = it.getParcelable(GAME_TYPE)!!
            players = it.getParcelableArray(PLAYERS) as Array<Player>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishGameBinding.inflate(inflater, container, false)
        viewModelFactory = GameViewModelFactory(
            application = requireActivity().application,
            players = players,
            gameType = gameType
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[FinishGameViewModel::class.java]

        setResultData(viewModel.getResult())
        setupControls()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            finishGameListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement listener!")
        }
    }

    private fun setResultData(result: List<Pair<String, Int>>) {

        binding.tvP1NameFinish.text = String.format(getString(R.string.player_name),result[0].first)
        binding.tvP1CountFinish.text = result[0].second.toString()
        binding.tvP1CountFinish.setTextColor(getTextColor(result[0].second))

        binding.tvP2NameFinish.text = String.format(getString(R.string.player_name),result[1].first)
        binding.tvP2CountFinish.text = result[1].second.toString()
        binding.tvP2CountFinish.setTextColor(getTextColor(result[1].second))

        if (gameType == GameType.THREE_PLAYER_GAME) {
            binding.tvP3NameFinish.text = String.format(getString(R.string.player_name),result[2].first)
            binding.tvP3CountFinish.text = result[2].second.toString()
            binding.tvP3CountFinish.setTextColor(getTextColor(result[2].second))
        }

    }

    private fun getTextColor(result: Int): Int {
        return when {
            result > 0 -> {
                ContextCompat.getColor(requireContext(),R.color.color_current_points_positive)
            }
            result < 0 -> {
                ContextCompat.getColor(requireContext(), R.color.color_current_points_negative)
            }
            else -> {
                ContextCompat.getColor(requireContext(), R.color.color_on_surface)
            }
        }
    }

    private fun setupControls() {
        binding.tvContinue.setOnClickListener{
            finishGameListener?.onFinishGame()
        }

        binding.tvCancel.setOnClickListener {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(gameType: GameType, players: Array<Player>) =
            FinishGameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(GAME_TYPE, gameType)
                    putParcelableArray(PLAYERS, players)
                }
            }
    }
}