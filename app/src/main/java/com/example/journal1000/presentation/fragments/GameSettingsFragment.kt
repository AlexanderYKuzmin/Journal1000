package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.R
import com.example.journal1000.databinding.FragmentGameSettingsBinding
import com.example.journal1000.domain.entity.GameType.THREE_PLAYER_GAME
import com.example.journal1000.domain.entity.GameType.TWO_PLAYER_GAME
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.viewmodels.SettingsViewModel
import java.lang.RuntimeException

private const val NUM_OF_PLAYERS = "number_of_players"
private const val NAME_ONE = "name_1"
private const val NAME_TWO = "name_2"
private const val NAME_THREE = "name_3"

class GameSettingsFragment : Fragment() {
    private var numberOfPlayers: Int = 3
    private var name1: String? = null
    private var name2: String? = null
    private var name3: String? = null

    private lateinit var binding: FragmentGameSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    private var onFragmentBehaviorControlManager: OnFragmentBehaviorControlManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            numberOfPlayers = it.getInt(NUM_OF_PLAYERS) ?: 3
            name1 = it.getString(NAME_ONE)
            name2 = it.getString(NAME_TWO)
            name3 = it.getString(NAME_THREE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        viewModel.numberOfPlayers.observe(viewLifecycleOwner) {
            setPlayersInputFields(it)
        }

        setNumberOfPlayerSelection()
        setControls()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            onFragmentBehaviorControlManager = context
        } else {
            throw RuntimeException("Activity must implement listener!")
        }
    }

    private fun setNumberOfPlayerSelection() {
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numberOfPlayers,
            android.R.layout.simple_spinner_dropdown_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPlayers.adapter = adapter
        binding.spPlayers.setSelection(
            when(numberOfPlayers) {
                3 -> 1
                2 -> 0
                -1 -> 1
                else -> { throw RuntimeException("Wrong number of players $numberOfPlayers") }
            }
        )
        viewModel.handleNumberOfPlayersSelection(numberOfPlayers)

        binding.spPlayers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val choose: Array<String> = resources.getStringArray(R.array.numberOfPlayers)
                viewModel.handleNumberOfPlayersSelection(Integer.parseInt(choose[p2]))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setPlayersInputFields(numberOfPlayers: Int) {
        if (numberOfPlayers == 2) binding.tilP3Name.visibility = View.GONE
        else binding.tilP3Name.visibility = View.VISIBLE

        if (name1 != null) {
            binding.etPlayerOneName.setText(name1)
            binding.etPlayerTwoName.setText(name2)
            if (binding.tilP3Name.visibility == View.VISIBLE) {
                binding.etPlayerThreeName.setText(name3)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setControls() {
        binding.tvContinue.setOnClickListener {
            val playerNames = when (viewModel.numberOfPlayers.value) {
                3 -> arrayOf(
                    binding.etPlayerOneName.text.toString(),
                    binding.etPlayerTwoName.text.toString(),
                    binding.etPlayerThreeName.text.toString()
                )
                2 -> arrayOf(
                    binding.etPlayerOneName.text.toString(),
                    binding.etPlayerTwoName.text.toString()
                )
                else -> throw RuntimeException("Incorrect players number")
            }
            viewModel.handleAcceptingGameSettings(playerNames)
            finishEdition(true)
        }

        binding.tvCancel.setOnClickListener {
            finishEdition(false)
        }
    }

    private fun finishEdition(isSettingsComplete: Boolean) {
        requireActivity().currentFocus?.clearFocus()
        if (isSettingsComplete) onFragmentBehaviorControlManager?.onFinishSettingsEdition(viewModel.names)
        else requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(numberOfPlayers: Int, name1: String?, name2: String?, name3: String?): GameSettingsFragment {
            return GameSettingsFragment().apply {
                arguments = Bundle().apply {
                    putInt(NUM_OF_PLAYERS, numberOfPlayers)
                    putString(NAME_ONE, name1)
                    putString(NAME_TWO, name2)
                    putString(NAME_THREE, name3)
                }
            }
        }
    }
}