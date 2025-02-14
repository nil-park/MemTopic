package com.nolbee.memtopic.utils

object ContentParser {
    fun parseContentToSentences(content: String): List<String> {
        val sentences = mutableListOf<String>()
        var i0 = 0
        content.forEachIndexed { i, c ->
            when (c) {
                '.', '?', '!' -> {
                    if (
                        i + 1 < content.length && content[i + 1].isWhitespace()
                        || i + 1 == content.length
                    ) {
                        sentences.add(content.substring(i0, i + 1))
                        i0 = i + 1
                    }
                }

                '\n', '\r' -> {
                    sentences.add(content.substring(i0, i + 1))
                    i0 = i + 1
                }
            }
        }
        return sentences
            .filterNot { it.isBlank() }
            .map { it.trim() }
    }
}
