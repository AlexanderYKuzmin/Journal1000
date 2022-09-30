package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.databinding.FragmentGamesBinding
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.SwipeToDeleteCallback
import com.example.journal1000.presentation.adapters.GamesAdapter
import com.example.journal1000.presentation.viewmodels.GameViewModelFactory
import com.example.journal1000.presentation.viewmodels.ListGamesViewModel
import java.lang.RuntimeException

private const val START_DATE = "start_date"
private const val END_DATE = "end_date"

class GamesFragment : Fragment() {
    private lateinit var startDate: String
    private lateinit var endDate: String

    private lateinit var binding: FragmentGamesBinding
    private lateinit var viewModelFactory: GameViewModelFactory
    private lateinit var viewModel: ListGamesViewModel

    private var selectGameListener: OnFragmentBehaviorControlManager? = null

    private lateinit var adapter: GamesAdapter

    //private lateinit var savePointsListener: OnFragmentBehaviorControlManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startDate = it.getString(START_DATE) ?: throw RuntimeException("No start date")
            endDate = it.getString(END_DATE) ?: throw RuntimeException("No end date")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGamesBinding.inflate(inflater, container, false)
        viewModelFactory = GameViewModelFactory(
            application = requireActivity().application
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ListGamesViewModel::class.java]
        adapter = GamesAdapter(requireContext())
        val rvGames: RecyclerView = binding.rvGames
        rvGames.adapter = adapter

        setUpClickListener()
        setUpSwipeListener(rvGames)

        viewModel.games.observe(viewLifecycleOwner) {
            //Log.d("observe gamelist", "gamelist size = ${it.size}")
            adapter.games = it
        }

        viewModel.loadGames(startDate, endDate)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            selectGameListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement listener!")
        }
    }

    private fun setUpClickListener() {
        adapter.onGameItemClickListener = {
            //viewModel.handleSelectGame(it)
            finishListGamesUsage(it)
        }
    }

    private fun setUpSwipeListener(recyclerView: RecyclerView) {
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteGame(adapter.games[viewHolder.adapterPosition])
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    private fun finishListGamesUsage(gameWithScores: GameWithScores) {
        activity?.currentFocus?.clearFocus()
        selectGameListener?.onGameSelected(gameWithScores)
    }

    override fun onDestroy() {
        Log.d("Games Fragment", "onDestroy")
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(startDate: String, endDate: String): GamesFragment {
            return GamesFragment().apply {
                arguments = Bundle().apply {
                    putString(START_DATE, startDate)
                    putString(END_DATE, endDate)
                }
            }
        }
    }
}