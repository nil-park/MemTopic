package com.nolbee.memtopic.utils

import java.text.BreakIterator
import java.util.Locale

object ContentParser {
    fun parseContentToSentences(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        
        // Handle line breaks that separate sentences without punctuation
        val processedContent = handleLineBreakSentences(content)
        
        // Normalize whitespace characters
        val normalizedContent = processedContent.replace(Regex("\\s+"), " ").trim()
        
        // Use BreakIterator for proper sentence segmentation
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
    
    private fun handleLineBreakSentences(text: String): String {
        val lines = text.split(Regex("[\r\n]+"))
        val processedLines = mutableListOf<String>()
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isNotBlank()) {
                // If line doesn't end with sentence punctuation, add period
                if (!trimmedLine.matches(Regex(".*[.!?。！？]\\s*$"))) {
                    processedLines.add("$trimmedLine.")
                } else {
                    processedLines.add(trimmedLine)
                }
            }
        }
        
        return processedLines.joinToString(" ")
    }
}
