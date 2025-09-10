package com.nolbee.memtopic.utils

import java.text.BreakIterator
import java.util.Locale

object ContentParser {
    fun parseContentToSentences(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        
        // First, handle paragraph separation by line breaks
        val paragraphs = content.split(Regex("[\\r\\n]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        val allSentences = mutableListOf<String>()
        
        for (paragraph in paragraphs) {
            // Check if paragraph needs punctuation at the end
            val processedParagraph = if (!paragraph.matches(Regex(".*[.!?。！？\"\"']\\s*$"))) {
                "$paragraph."
            } else {
                paragraph
            }
            
            // Parse sentences with quoted text handling
            val sentences = parseWithQuotedTextHandling(processedParagraph)
            allSentences.addAll(sentences)
        }
        
        return allSentences
    }
    
    private fun parseWithQuotedTextHandling(text: String): List<String> {
        // Store all tokenized parts and replace with tokens
        val tokenizedParts = mutableListOf<String>()
        
        // First, tokenize quoted text (double quotes)
        var processedText = text.replace(Regex("\"[^\"]*\"")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }
        
        // Handle single quotes
        processedText = processedText.replace(Regex("'[^']*'")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }
        
        // Handle unicode quotes
        processedText = processedText.replace(Regex("\"[^\"]*\"")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }
        
        // Tokenize common abbreviations to prevent incorrect splitting
        processedText = processedText.replace(Regex("\\b(Dr|Mr|Mrs|Ms|Prof|Sr|Jr|U\\.S\\.A|U\\.K|N\\.Y\\.C)\\.")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }
        
        // Handle decimal numbers
        processedText = processedText.replace(Regex("\\b\\d+\\.\\d+\\b")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }
        
        // Normalize whitespace characters
        val normalizedContent = processedText.replace(Regex("\\s+"), " ").trim()
        
        // Special case: if it's just punctuation, don't split it
        if (normalizedContent.matches(Regex("^[.!?\\s…]+$"))) {
            // Restore tokens before returning
            var restored = normalizedContent
            tokenizedParts.forEachIndexed { index, token ->
                restored = restored.replace("<<TOKEN_$index>>", token)
            }
            return listOf(restored)
        }
        
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
        
        // Post-process: handle cases where BreakIterator failed to split after tokenized abbreviations
        val fixedSentences = mutableListOf<String>()
        for (sentence in sentences) {
            // Simple split on ". [Capital letter]" pattern - much simpler approach
            val parts = sentence.split(Regex("\\. +(?=[A-Z])"))
            if (parts.size > 1) {
                // Add periods back to all but the last part
                for (i in 0 until parts.size - 1) {
                    fixedSentences.add("${parts[i]}.")
                }
                fixedSentences.add(parts.last())
            } else {
                fixedSentences.add(sentence)
            }
        }
        
        // Restore all tokenized parts
        return fixedSentences.map { sentence ->
            var restored = sentence
            tokenizedParts.forEachIndexed { index, token ->
                restored = restored.replace("<<TOKEN_$index>>", token)
            }
            restored
        }
    }
}
