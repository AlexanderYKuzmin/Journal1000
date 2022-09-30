package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.R
import com.example.journal1000.databinding.FragmentPointsStoreBinding
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.GameType.THREE_PLAYER_GAME
import com.example.journal1000.domain.entity.GameType.TWO_PLAYER_GAME
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.PlayerOrder
import com.example.journal1000.presentation.GameViewModel
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_ONE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_THREE
import com.example.journal1000.presentation.GameViewModel.Companion.PLAYER_TWO
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.viewmodels.GameViewModelFactory
import com.example.journal1000.presentation.viewmodels.PointsEditionViewModel
import java.lang.RuntimeException

private const val TYPE = "type"
private const val PLAYERS = "players"
private const val MODE = "mode"

class PointsEditionFragment : Fragment() {

    private var _binding: FragmentPointsStoreBinding? = null
    val binding: FragmentPointsStoreBinding
        get() = _binding ?: throw RuntimeException()

    private lateinit var viewModelFactory: GameViewModelFactory
    private lateinit var viewModel: PointsEditionViewModel

    private lateinit var finishEditionListener: OnFragmentBehaviorControlManager

    private lateinit var gameType: GameType
    private lateinit var players: Array<Player>
    private var mode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameType = it.getParcelable<GameType>(TYPE) ?: throw RuntimeException("No GameType transferred")
            players = it.getParcelableArray(PLAYERS) as Array<Player>
            mode = it.getInt(MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelFactory = GameViewModelFactory(
            application = requireActivity().application,
            mode = mode
        )
        _binding = FragmentPointsStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[PointsEditionViewModel::class.java]

        viewModel.mode = mode

        viewModel.errorInputPoints.observe(viewLifecycleOwner){
            handleInputErrors(it)
        }

        setTitle()
        setFieldsForAccordingGameType()
        addTextHints()
        setTextChangeListeners()
        setControlButtonsListener()

        if (mode == AUCTION_MODE) addDefaultAuctionData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            finishEditionListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement listener!")
        }
    }

    private fun setTitle() {
        binding.tvTitleSave.text =
            if (mode == SAVE_POINTS_MODE) getString(R.string.save_points)
            else getString(R.string.title_auction)
    }

    private fun setTextChangeListeners() {
        binding.etP1Points.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputPoints(PLAYER_ONE)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etP2Points.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputPoints(PLAYER_TWO)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        if (gameType == THREE_PLAYER_GAME) {
            binding.etP3Points.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.resetErrorInputPoints(PLAYER_THREE)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setControlButtonsListener() {
        binding.tvCancelSs.setOnClickListener {
            //viewModel.handleTestChangePoints()
            finishEdition()
        }
        binding.tvContinueSs.setOnClickListener {
            Log.d("tv continue", "button is pressed")
            val playersPoints: MutableList<String> = mutableListOf()
            playersPoints.add(binding.etP1Points.text.toString())
            playersPoints.add(binding.etP2Points.text.toString())
            if (gameType == THREE_PLAYER_GAME) {
                playersPoints.add(binding.etP3Points.text.toString())
            }

            if (viewModel.validateInput(playersPoints)) {
                if (viewModel.validateInputValuesAndSetUp(playersPoints, players)) {
                    if (mode == SAVE_POINTS_MODE)  finishEdition(viewModel.pointsInt)
                    else finishEdition(viewModel.getAuctionResult())
                } else Log.d("Points Edition Fragment", "inputs not valid")
            }
        }
    }

    private fun setFieldsForAccordingGameType() {
        if (gameType == TWO_PLAYER_GAME) binding.tilP3Points.visibility = View.GONE
    }

    private fun addTextHints() {
        with(binding) {
            tilP1Points.hint = players[PLAYER_ONE].name
            tilP2Points.hint = players[PLAYER_TWO].name
            if (gameType == THREE_PLAYER_GAME) tilP3Points.hint = players[PLAYER_THREE].name
        }
    }

    private fun addDefaultAuctionData() {
        with(binding) {
            val tvViewArray = arrayOf(etP1Points, etP2Points, etP3Points)
            for (i in players.indices) {
                if (players[i].onHundred) tvViewArray[i].setText(getString(R.string.hundred))
            }
        }
    }

    private fun handleInputErrors(errors: Array<Boolean>) {
        val etArray = with(binding) {
            arrayOf(etP1Points, etP2Points, etP3Points)
        }
        for (i in errors.indices) {
            if (errors[i]) etArray[i].error = "Ошибка ввода"
        }
    }

    private fun finishEdition(points: List<Int> = emptyList()) {
        activity?.currentFocus?.clearFocus()
        finishEditionListener.onFinishSavePointsEdition(points)
    }

    private fun finishEdition(auctionData: Pair<PlayerOrder, Int>) {
        activity?.currentFocus?.clearFocus()
        finishEditionListener.onFinishAuctionEdition(auctionData)
    }

    companion object {
        const val SAVE_POINTS_MODE = 100
        const val AUCTION_MODE = 101

        @JvmStatic
        fun newInstance(gameType: GameType, players: Array<Player>, mode: Int): PointsEditionFragment {
            return PointsEditionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TYPE, gameType)
                    putParcelableArray(PLAYERS, players)
                    putInt(MODE, mode)
                }
            }
        }

        /*@JvmStatic
        fun newInstance(mode: Int): PointsEditionFragment {
            return PointsEditionFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
        }*/
    }
}