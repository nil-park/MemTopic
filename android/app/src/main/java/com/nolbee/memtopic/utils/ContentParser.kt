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
            // Parse sentences with quoted text handling
            val sentences = parseWithQuotedTextHandling(paragraph)
            allSentences.addAll(sentences)
        }

        return allSentences
    }

    private fun parseWithQuotedTextHandling(text: String): List<String> {
        val tokenizedParts = mutableListOf<String>()
        val processedText = tokenizeSpecialContent(text, tokenizedParts)
        val normalizedContent = normalizeWhitespace(processedText)

        if (isPurelyPunctuation(normalizedContent)) {
            return listOf(restoreTokens(normalizedContent, tokenizedParts))
        }

        val sentences = segmentWithBreakIterator(normalizedContent)
        val fixedSentences = postProcessSentences(sentences)

        return restoreTokensInSentences(fixedSentences, tokenizedParts)
    }

    internal fun tokenizeSpecialContent(text: String, tokenizedParts: MutableList<String>): String {
        var processedText = text

        // Tokenize quoted text (double quotes)
        processedText = processedText.replace(Regex("\"[^\"]*\"")) { match ->
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

        // Tokenize common abbreviations
        processedText =
            processedText.replace(Regex("\\b(Dr|Mr|Mrs|Ms|Prof|Sr|Jr|U\\.S\\.A|U\\.K|N\\.Y\\.C)\\.")) { match ->
                tokenizedParts.add(match.value)
                "<<TOKEN_${tokenizedParts.size - 1}>>"
            }

        // Handle decimal numbers
        processedText = processedText.replace(Regex("\\b\\d+\\.\\d+\\b")) { match ->
            tokenizedParts.add(match.value)
            "<<TOKEN_${tokenizedParts.size - 1}>>"
        }

        return processedText
    }

    internal fun normalizeWhitespace(text: String): String {
        return text.replace(Regex("\\s+"), " ").trim()
    }

    internal fun isPurelyPunctuation(text: String): Boolean {
        return text.matches(Regex("^[.!?\\s…]+$"))
    }

    private fun segmentWithBreakIterator(text: String): List<String> {
        val iterator = BreakIterator.getSentenceInstance(Locale.getDefault())
        iterator.setText(text)

        val sentences = mutableListOf<String>()
        var start = iterator.first()

        while (start != BreakIterator.DONE) {
            val end = iterator.next()
            if (end != BreakIterator.DONE) {
                val sentence = text.substring(start, end).trim()
                if (sentence.isNotBlank()) {
                    sentences.add(sentence)
                }
            }
            start = end
        }

        return sentences
    }

    private fun postProcessSentences(sentences: List<String>): List<String> {
        val fixedSentences = mutableListOf<String>()

        for (sentence in sentences) {
            val parts = sentence.split(Regex("\\. +(?=[A-Z])"))
            if (parts.size > 1) {
                for (i in 0 until parts.size - 1) {
                    fixedSentences.add("${parts[i]}.")
                }
                fixedSentences.add(parts.last())
            } else {
                fixedSentences.add(sentence)
            }
        }

        return fixedSentences
    }

    internal fun restoreTokens(text: String, tokenizedParts: List<String>): String {
        var restored = text
        tokenizedParts.forEachIndexed { index, token ->
            restored = restored.replace("<<TOKEN_$index>>", token)
        }
        return restored
    }

    private fun restoreTokensInSentences(
        sentences: List<String>,
        tokenizedParts: List<String>
    ): List<String> {
        return sentences.map { sentence -> restoreTokens(sentence, tokenizedParts) }
    }
}
