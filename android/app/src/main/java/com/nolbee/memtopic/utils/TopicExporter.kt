package com.nolbee.memtopic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nolbee.memtopic.database.Topic
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Custom serializer for Date objects to ISO 8601 format
 */
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(dateFormat.format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        return dateFormat.parse(decoder.decodeString()) ?: Date()
    }
}

/**
 * Serializable wrapper for Topic data
 */
@Serializable
data class SerializableTopic(
    val title: String,
    val content: String,
    val options: String
)

/**
 * JSON export schema with metadata
 */
@Serializable
data class TopicExportData(
    val version: String = "1.0",
    @SerialName("app_version")
    val appVersion: String = "1.0",
    @SerialName("export_date")
    @Serializable(with = DateSerializer::class)
    val exportDate: Date = Date(),
    @SerialName("topic_count")
    val topicCount: Int,
    val topics: List<SerializableTopic>
)

/**
 * Utility class for exporting topics to JSON files
 */
class TopicExporter(private val context: Context) {

    private val json = Json { prettyPrint = true }

    /**
     * Convert Topic to SerializableTopic
     */
    fun Topic.toSerializableTopic(): SerializableTopic {
        return SerializableTopic(
            title = this.title,
            content = this.content,
            options = this.options
        )
    }

    /**
     * Create export data from list of topics
     */
    fun createExportData(topics: List<Topic>): TopicExportData {
        return TopicExportData(
            topicCount = topics.size,
            topics = topics.map { it.toSerializableTopic() }
        )
    }

    /**
     * Convert topics to JSON string
     */
    fun topicsToJson(topics: List<Topic>): String {
        val exportData = createExportData(topics)
        return json.encodeToString(TopicExportData.serializer(), exportData)
    }

    /**
     * Create file save intent for Storage Access Framework
     */
    fun createSaveFileIntent(): Intent {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "memtopic_topics_${dateFormat.format(Date())}.json"

        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
    }

    /**
     * Write JSON data to URI using Storage Access Framework
     */
    fun writeJsonToUri(uri: Uri, jsonData: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonData.toByteArray())
                outputStream.flush()
            }
            true
        } catch (e: SecurityException) {
            // Permission denied - this shouldn't happen with SAF but just in case
            e.printStackTrace()
            false
        } catch (e: java.io.FileNotFoundException) {
            // File not accessible
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}