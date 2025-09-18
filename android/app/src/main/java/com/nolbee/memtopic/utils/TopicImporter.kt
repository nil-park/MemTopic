package com.nolbee.memtopic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nolbee.memtopic.database.ITopicRepository
import com.nolbee.memtopic.database.Topic
import kotlinx.serialization.json.Json
import java.util.Date

/**
 * Result class for import operations
 */
sealed class ImportResult {
    data class Success(val importedCount: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

/**
 * Utility class for importing topics from JSON files
 */
class TopicImporter(
    private val context: Context,
    private val repository: ITopicRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Create file open intent for Storage Access Framework
     */
    fun createOpenFileIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
    }

    /**
     * Read JSON data from URI using Storage Access Framework
     */
    private fun readJsonFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Explicitly use UTF-8 encoding for proper Korean/Unicode support
                inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: SecurityException) {
            // Permission denied - this shouldn't happen with SAF but just in case
            e.printStackTrace()
            null
        } catch (e: java.io.FileNotFoundException) {
            // File not accessible or not found
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parse and validate JSON data
     */
    private fun parseAndValidateJson(jsonString: String): TopicExportData? {
        return try {
            // Check if string is empty or just whitespace
            if (jsonString.isBlank()) {
                return null
            }

            val exportData = json.decodeFromString<TopicExportData>(jsonString)

            // Basic validation - allow empty topics for import
            // (사용자가 빈 파일을 import할 수도 있음)

            // Validate each topic has required fields
            exportData.topics.forEach { topic ->
                if (topic.title.isBlank()) {
                    return null
                }
            }

            exportData
        } catch (e: kotlinx.serialization.SerializationException) {
            // JSON 구조나 형식이 잘못됨
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convert SerializableTopic to Topic with duplicate title handling
     */
    private fun SerializableTopic.toTopic(existingTitles: Set<String>): Topic {
        val finalTitle = generateUniqueTitle(this.title, existingTitles)

        return Topic(
            title = finalTitle,
            content = this.content,
            options = this.options,
            lastModified = Date(),
            lastPlayback = Date()
        )
    }

    /**
     * Generate unique title by adding number suffix if duplicate exists
     */
    private fun generateUniqueTitle(originalTitle: String, existingTitles: Set<String>): String {
        if (!existingTitles.contains(originalTitle)) {
            return originalTitle
        }

        var counter = 1
        var newTitle: String
        do {
            newTitle = "$originalTitle ($counter)"
            counter++
        } while (existingTitles.contains(newTitle))

        return newTitle
    }

    /**
     * Import topics from JSON file URI
     */
    suspend fun importTopicsFromUri(uri: Uri): ImportResult {
        return try {
            // Read JSON from URI
            val jsonString = readJsonFromUri(uri)
                ?: return ImportResult.Error("파일을 읽을 수 없습니다. 파일이 존재하고 접근 가능한지 확인해주세요.")

            // Parse and validate JSON
            val exportData = parseAndValidateJson(jsonString)
                ?: return ImportResult.Error("유효하지 않은 JSON 파일입니다. MemTopic에서 내보낸 파일인지 확인해주세요.")

            // Get existing topic titles to handle duplicates
            val existingTopics = repository.getAllTopics()
            val existingTitles = existingTopics.map { it.title }.toMutableSet()

            // Convert SerializableTopics to Topics with duplicate handling
            val topicsToImport = mutableListOf<Topic>()
            exportData.topics.forEach { serializableTopic ->
                val topic = serializableTopic.toTopic(existingTitles)
                topicsToImport.add(topic)
                existingTitles.add(topic.title) // Update set for next iteration
            }

            // Log info for large imports
            if (topicsToImport.size > 50) {
                println("MemTopic: Importing large dataset with ${topicsToImport.size} topics")
            }

            // Bulk insert topics for better performance with large datasets
            if (topicsToImport.isNotEmpty()) {
                repository.upsertTopics(topicsToImport)
            }

            ImportResult.Success(topicsToImport.size)
        } catch (e: Exception) {
            e.printStackTrace()
            ImportResult.Error("가져오기 중 오류가 발생했습니다: ${e.message}")
        }
    }
}