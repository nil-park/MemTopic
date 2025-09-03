package com.nolbee.memtopic.utils

import java.text.BreakIterator
import java.util.Locale

object ContentParser {
    fun parseContentToSentences(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        
        // Normalize whitespace characters
        val normalizedContent = content.replace(Regex("\\s+"), " ").trim()
        
        val iterator = BreakIterator.getSentenceInstance(Locale.getDefault())
        iterator.setText(normalizedContent)
        
        val sentences = mutableListOf<String>()
        var start = iterator.first()
        
        while (start != BreakIterator.DONE) {
            val end = iterator.next()
            if (end != BreakIterator.DONE) {
                val sentence = normalizedContent.substring(start, end).trim()
                if (sentence.isNotBlank()) {
                    sentences.add(sentence)
                }
            }
            start = end
        }
        
        return sentences
    }
}
