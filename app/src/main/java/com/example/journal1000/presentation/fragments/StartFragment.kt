package com.example.journal1000.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.R
import com.example.journal1000.presentation.GameViewModel


class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]

        val iv = view.findViewById<ImageView>(R.id.iv_journal)
        iv.alpha = 0.2f
        iv.animate().apply {
            interpolator = LinearInterpolator()
            duration = 1000
            alpha(1f)
            startDelay = 500
            start()
        }

        val btn_start = view.findViewById<Button>(R.id.btn_start_game)
        btn_start.text = if (viewModel.prefs.id == -1L) {
            getString(R.string.new_game)
        } else {
            getString(R.string.load_last_game)
        }

        btn_start.alpha = 0.0f
        btn_start.animate().apply {
            interpolator = LinearInterpolator()
            duration = 500
            alpha(1f)
            startDelay = 1500
            start()
        }
        btn_start.setOnClickListener {
            if (viewModel.prefs.id == -1L) viewModel.handleNew()
            else viewModel.handleLastGameLoading()
        }

        val tv = view.findViewById<TextView>(R.id.tv_1000)
        tv.alpha = 0.0f
        tv.animate().apply {
            interpolator = LinearInterpolator()
            duration = 500
            alpha(1f)
            startDelay = 1500
            start()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}