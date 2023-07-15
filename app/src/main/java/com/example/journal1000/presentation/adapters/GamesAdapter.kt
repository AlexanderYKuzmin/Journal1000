package com.example.journal1000.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.R
import com.example.journal1000.databinding.LayoutGamesItem2PBinding
import com.example.journal1000.databinding.LayoutGamesItem3PBinding
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.extensions.format
import com.example.journal1000.extensions.substringIfOutOfRange

class GamesAdapter(val context: Context): RecyclerView.Adapter<GamesAdapter.GamesViewHolder>() {
    private val range = IntRange(0, 8)
    var games: List<GameWithScores> = arrayListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onGameItemClickListener: ((GameWithScores) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val layout = when (viewType) {
            TWO_PLAYER_GAME -> R.layout.layout_games_item_2_p
            THREE_PLAYER_GAME -> R.layout.layout_games_item_3_p
            else -> throw RuntimeException("Unknown view type!")
        }

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false)

        return GamesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        with(holder) {
            with(games[position]) {
                when (binding) {
                    is LayoutGamesItem3PBinding -> {
                        binding.tvGameId.text = game.gameId.toString()
                        binding.tvDate.text = game.gameDate.format("dd.MM.yyyy")
                        binding.tvP1Name.text = players[0].name.substringIfOutOfRange(range)
                        binding.tvP2Name.text = players[1].name.substringIfOutOfRange(range)
                        binding.tvP3Name.text = players[2].name.substringIfOutOfRange(range)
                        binding.tvGamesP1Count.text = players[0].count.toString()
                        binding.tvGamesP2Count.text = players[1].count.toString()
                        binding.tvGamesP3Count.text = players[2].count.toString()
                        if (game.isGameFinished) binding.ivFinished.visibility = View.VISIBLE
                        else binding.ivFinished.visibility = View.GONE
                    }
                    is LayoutGamesItem2PBinding -> {
                        binding.tvGameId.text = game.gameId.toString()
                        binding.tvDate.text = game.gameDate.format("dd.MM.yyyy")
                        binding.tvP1Name.text = players[0].name.substringIfOutOfRange(range)
                        binding.tvP2Name.text = players[1].name.substringIfOutOfRange(range)
                        binding.tvGamesP1Count.text = players[0].count.toString()
                        binding.tvGamesP2Count.text = players[1].count.toString()
                        if (game.isGameFinished) binding.ivFinished.visibility = View.VISIBLE
                        else binding.ivFinished.visibility = View.GONE
                    }
                }
            }

            binding.root.setOnClickListener {
                Log.d("Games Adapter", "OnClickListener from holder. game id = ${games[position].game.gameId}")
                onGameItemClickListener?.invoke(games[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun getItemViewType(position: Int): Int {
        return games[position].game.type.value
    }

    companion object {
        private const val THREE_PLAYER_GAME = 3
        private const val TWO_PLAYER_GAME = 2
    }
    inner class GamesViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}