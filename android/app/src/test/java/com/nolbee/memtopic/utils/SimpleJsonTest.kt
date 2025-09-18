package com.nolbee.memtopic.utils

import com.nolbee.memtopic.database.Topic
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.Date

/**
 * Simple tests for JSON serialization/deserialization without Android dependencies
 */
class SimpleJsonTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun `test JSON serialization and deserialization`() {
        // Given
        val topic = Topic(
            title = "Test Topic",
            content = "Test content",
            options = """{"language": "ko-KR"}"""
        )

        val serializableTopic = SerializableTopic(
            title = topic.title,
            content = topic.content,
            options = topic.options
        )

        val exportData = TopicExportData(
            version = "1.0",
            appVersion = "1.0",
            exportDate = Date(),
            topicCount = 1,
            topics = listOf(serializableTopic)
        )

        // When - Serialize to JSON
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)

        // Then - Should contain expected content
        assertTrue(jsonString.contains("Test Topic"))
        assertTrue(jsonString.contains("Test content"))
        assertTrue(jsonString.contains("ko-KR"))

        // When - Deserialize back from JSON
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then - Should match original data
        assertEquals(1, parsedData.topicCount)
        assertEquals("Test Topic", parsedData.topics[0].title)
        assertEquals("Test content", parsedData.topics[0].content)
    }

    @Test
    fun `test Korean character handling`() {
        // Given
        val koreanTopic = SerializableTopic(
            title = "한글 제목",
            content = "한글 내용입니다.",
            options = """{"언어": "한국어"}"""
        )

        val exportData = TopicExportData(
            topicCount = 1,
            topics = listOf(koreanTopic)
        )

        // When
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then
        assertEquals("한글 제목", parsedData.topics[0].title)
        assertEquals("한글 내용입니다.", parsedData.topics[0].content)
        assertTrue(parsedData.topics[0].options.contains("한국어"))
    }

    @Test
    fun `test special characters handling`() {
        // Given
        val specialTopic = SerializableTopic(
            title = "Title with \"quotes\" and \\backslashes\\",
            content = "Content with\nnewlines and\ttabs",
            options = """{"test": "value with \"quotes\""}"""
        )

        val exportData = TopicExportData(
            topicCount = 1,
            topics = listOf(specialTopic)
        )

        // When
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then
        assertEquals("Title with \"quotes\" and \\backslashes\\", parsedData.topics[0].title)
        assertEquals("Content with\nnewlines and\ttabs", parsedData.topics[0].content)
    }

    @Test
    fun `test empty topics list`() {
        // Given
        val exportData = TopicExportData(
            topicCount = 0,
            topics = emptyList()
        )

        // When
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then
        assertEquals(0, parsedData.topicCount)
        assertTrue(parsedData.topics.isEmpty())
    }

    @Test
    fun `test malformed JSON handling`() {
        // Given
        val malformedJson = """{"invalid": "json structure without required fields"}"""

        // When/Then
        try {
            json.decodeFromString<TopicExportData>(malformedJson)
            fail("Should have thrown exception for malformed JSON")
        } catch (e: Exception) {
            // Expected behavior - missing required fields should cause exception
            assertTrue(e is kotlinx.serialization.SerializationException)
        }
    }

    @Test
    fun `test large content handling`() {
        // Given
        val largeTopic = SerializableTopic(
            title = "Large Topic",
            content = "Large content: " + "A".repeat(10000), // 10KB content
            options = "{}"
        )

        val exportData = TopicExportData(
            topicCount = 1,
            topics = listOf(largeTopic)
        )

        // When
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then
        assertEquals("Large Topic", parsedData.topics[0].title)
        assertTrue(parsedData.topics[0].content.length > 10000)
    }

    @Test
    fun `test multiple topics with TestDataGenerator`() {
        // Given
        val topics = TestDataGenerator.generateMultipleTopics(3, includeKorean = true)
        val serializableTopics = topics.map { topic ->
            SerializableTopic(
                title = topic.title,
                content = topic.content,
                options = topic.options
            )
        }

        val exportData = TopicExportData(
            topicCount = topics.size,
            topics = serializableTopics
        )

        // When
        val jsonString = json.encodeToString(TopicExportData.serializer(), exportData)
        val parsedData = json.decodeFromString<TopicExportData>(jsonString)

        // Then
        assertEquals(3, parsedData.topicCount)
        assertEquals(3, parsedData.topics.size)

        // Should have Korean topics when includeKorean = true
        assertTrue(parsedData.topics.any { it.title.contains("한글") || it.title.contains("Test") })
    }
}