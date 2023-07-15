package com.example.journal1000.presentation.markdown

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.SpannedString
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.example.journal1000.R
import com.example.journal1000.data.markdown.Element
import com.example.journal1000.data.markdown.MarkdownParser
import com.example.journal1000.data.markdown.MarkdownText
import com.example.journal1000.extensions.attrValue
import com.example.journal1000.extensions.dpToPx
import com.example.journal1000.presentation.spans.HeaderSpan
import com.example.journal1000.presentation.spans.UnorderedListSpan

class MarkdownBuilder(context: Context) {

    private val colorSecondary = context.attrValue(R.attr.colorSecondary)
    private val colorPrimary = context.attrValue(R.attr.colorPrimary)
    private val colorDivider = context.getColor(R.color.color_divider)
    private val gap: Float = context.dpToPx(8)
    private val bulletRadius = context.dpToPx(3)
    private val headerMarginTop = context.dpToPx(12)
    private val headerMarginBottom = context.dpToPx(8)

    fun markdownToSpan(textContent: String): SpannedString {
        val markdown: MarkdownText = MarkdownParser.parse(textContent)

        return buildSpannedString {
            markdown.elements.forEach { buildElement(it, this) }
        }
    }

    private fun buildElement(element: Element, builder: SpannableStringBuilder): CharSequence {
        return builder.apply {
            when (element) {
                is Element.Text -> append(element.text)
                is Element.UnorderedListItem -> {
                    inSpans(UnorderedListSpan(gap, bulletRadius, colorSecondary)) {
                        for (child in element.elements) {
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Header -> {
                    inSpans(
                        HeaderSpan(
                            element.level,
                            colorPrimary,
                            colorDivider,
                            headerMarginTop,
                            headerMarginBottom
                        )
                    ) {
                        append(element.text)
                    }
                }
                else -> append(element.text)
            }
        }
    }
}