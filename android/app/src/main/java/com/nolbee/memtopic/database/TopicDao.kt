package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Entity
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val lastModified: Date = Date(),
    val lastPlayback: Date = Date(),
    val content: String = "",
    val options: String = """{"languageCode":"en-US","voiceType":"en-US-Neural2-J"}""", // JSON
)

@Dao
interface TopicDao {
    @Upsert
    suspend fun upsertTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("SELECT * FROM topic WHERE id = :id")
    suspend fun getTopic(id: Int): Topic?

    @Query("SELECT * FROM topic ORDER BY title ASC")
    fun selectTopicByName(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastModified DESC")
    fun selectTopicByLastModified(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastPlayback DESC")
    fun selectTopicByLastPlayback(): Flow<List<Topic>>
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
val sampleTopic00 = Topic(
    id = 1,
    title = "Insertion Sort",
    lastModified = dateFormat.parse("2023-09-12") ?: Date(),
    lastPlayback = dateFormat.parse("2023-09-28") ?: Date(),
    content = """
        We start with insertion sort, which is an efficient algorithm for sorting a small number of elements.
        Insertion sort works the way many people sort a hand of playing cards.
        We start with an empty left hand and the cards face down on the table.
        We then remove one card at a time from the table
        and insert it into the correct position in the left hand.
        To find the correct position for a card,
        we compare it with each of the cards already in the hand,
        from right to left, as illustrated in Figure 2.1.
        At all times, the cards held in the left hand are sorted,
        and these cards were originally the top cards of the pile on the table.
    """.trimIndent(),
    options = "{}",
)

val sampleTopic01 = Topic(
    id = 2,
    title = "Alias Method",
    lastModified = dateFormat.parse("2023-06-21") ?: Date(),
    lastPlayback = dateFormat.parse("2024-06-20") ?: Date(),
    content = """
        At a high level, the alias method works as follows.
        First, we create rectangles to represent the different probabilities of the dice sides.
        Next, we divide and rearrange those rectangles to fill a rectangular target completely.
        Each column in the target has a fixed width and contains rectangles from at most two different sides of the loaded die.
        Finally, we simulate die rolls by randomly throwing darts at the target, which we can achieve by using a combination of a fair die and a biased coin flip.
    """.trimIndent(),
    options = "{}",
)

val sampleTopic02 = Topic(
    id = 3,
    title = "Cloud Computing",
    lastModified = dateFormat.parse("2024-07-07") ?: Date(),
    lastPlayback = dateFormat.parse("2024-07-15") ?: Date(),
    content = """
        Cloud computing is the on-demand availability of computing resources as services over the internet.
        It eliminates the need for individuals and businesses to self-manage physical resources themselves,
        and only pay for what they use.
        Public clouds are run by third-party cloud service providers.
        They offer compute, storage, and network resources over the internet,
        enabling companies to access shared on-demand resources
        based on their unique requirements and business goals.
        Private clouds are built, managed, and owned by a single organization
        and privately hosted in their own data centers, commonly known as “on-premises” or “on-prem.”
        They provide greater control, security, and management of data
        while still enabling internal users to benefit from a shared pool of compute, storage, and network resources.
        Hybrid clouds combine public and private cloud models,
        allowing companies to leverage public cloud services
        and maintain the security and compliance capabilities commonly found in private cloud architectures.
    """.trimIndent(),
    options = "{}",
)
