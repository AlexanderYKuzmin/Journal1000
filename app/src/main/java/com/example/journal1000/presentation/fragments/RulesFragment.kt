package com.example.journal1000.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.journal1000.data.DataHolder
import com.example.journal1000.data.markdown.MarkdownParser
import com.example.journal1000.databinding.FragmentGameScoreList3PBinding
import com.example.journal1000.databinding.FragmentRulesBinding
import com.example.journal1000.presentation.OnFragmentBehaviorControlManager
import com.example.journal1000.presentation.markdown.MarkdownBuilder

class RulesFragment: Fragment() {

    private lateinit var binding: FragmentRulesBinding

    private var removeRulesSheetListener: OnFragmentBehaviorControlManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.tvRules) {
            setMovementMethod(ScrollingMovementMethod())
            setText(MarkdownBuilder(context).markdownToSpan(DataHolder.rulesText))
        }

        setupControls()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentBehaviorControlManager) {
            removeRulesSheetListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement listener!")
        }
    }

    private fun setupControls() {
        binding.tvOkRules.setOnClickListener {
            removeRulesSheetListener?.removeRulesSheet()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): RulesFragment {
            return RulesFragment()
        }
    }
}