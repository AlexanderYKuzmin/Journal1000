package com.example.journal1000.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.journal1000.R
import com.example.journal1000.databinding.LayoutGameListItemBinding

class GameListAdapter(val context: Context) : RecyclerView.Adapter<GameListAdapter.GameListViewHolder>() {

     var gameList: List<GameListItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        return GameListViewHolder(LayoutGameListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        with(holder) {
            with(gameList[position]) {
                binding.tvP1Count.text = p1Count.toString()
                binding.tvP2Count.text = p2Count.toString()
                binding.tvP3Count.text = p3Count.toString()

                binding.tvP1CountCurrent.text = String.format("%d", p1CountCurrent)
                binding.tvP1CountCurrent.setTextColor(getTextColor(p1CountCurrent))

                binding.tvP2CountCurrent.text = String.format("%d", p2CountCurrent)
                binding.tvP2CountCurrent.setTextColor(getTextColor(p2CountCurrent))

                binding.tvP3CountCurrent.text = String.format("%d", p3CountCurrent)
                binding.tvP3CountCurrent.setTextColor(getTextColor(p3CountCurrent))
            }
        }
    }

    override fun getItemCount(): Int = gameList.size

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

    inner class GameListViewHolder(val binding: LayoutGameListItemBinding) : RecyclerView.ViewHolder(binding.root)
}