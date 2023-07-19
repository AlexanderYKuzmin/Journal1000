package com.example.journal1000.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.R
import com.example.journal1000.databinding.LayoutGameListItem2PBinding

class GameList2PAdapter(val context: Context) : RecyclerView.Adapter<GameList2PAdapter.GameList2PViewHolder>() {

    var gameList: List<GameListItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameList2PAdapter.GameList2PViewHolder {
        return GameList2PViewHolder(LayoutGameListItem2PBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: GameList2PAdapter.GameList2PViewHolder, position: Int) {
        with(holder) {
            with(gameList[position]) {
                binding.tvP1Count2.text = p1Count.toString()
                binding.tvP2Count2.text = p2Count.toString()

                binding.tvP1CountCurrent2.text = String.format("%d", p1CountCurrent)
                binding.tvP1CountCurrent2.setTextColor(getTextColor(p1CountCurrent))

                binding.tvP2CountCurrent2.text = String.format("%d", p2CountCurrent)
                binding.tvP2CountCurrent2.setTextColor(getTextColor(p2CountCurrent))
            }
        }
    }

    override fun getItemCount(): Int {
       return gameList.size
    }

    private fun getTextColor(playerCurrentCount: Int): Int {
        return when {
            playerCurrentCount > 0 -> {
                ContextCompat.getColor(context, R.color.color_current_points_positive)
            }
            playerCurrentCount < 0 -> {
                ContextCompat.getColor(context, R.color.color_current_points_negative)
            }
            else -> {
                ContextCompat.getColor(context, R.color.color_article_bar)
            }
        }
    }

    inner class GameList2PViewHolder(val binding: LayoutGameListItem2PBinding) : RecyclerView.ViewHolder(binding.root)
}