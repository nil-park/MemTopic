package com.nolbee.memtopic.utils

import com.nolbee.memtopic.database.Topic
import java.util.Date

/**
 * Simple utility for generating test topics
 */
object TestDataGenerator {

    /**
     * Generate multiple test topics for testing
     */
    fun generateMultipleTopics(count: Int, includeKorean: Boolean = false): List<Topic> {
        val topics = mutableListOf<Topic>()

        repeat(count) { index ->
            val title = if (includeKorean && index % 2 == 0) {
                "한글 제목 #${index + 1}"
            } else {
                "Test Topic #${index + 1}"
            }

            topics.add(
                Topic(
                    id = index + 1,
                    title = title,
                    content = "Test content for topic ${index + 1}",
                    options = """{"language": "test"}""",
                    lastModified = Date(),
                    lastPlayback = Date()
                )
            )
        }

        return topics
    }
}