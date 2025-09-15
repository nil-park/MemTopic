package com.nolbee.memtopic.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Custom serializer for Date objects to ISO 8601 format
 */
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
    
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