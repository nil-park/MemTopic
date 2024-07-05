package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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
)

@Dao
interface TopicDao {
    @Upsert
    suspend fun upsertTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("SELECT * FROM topic ORDER BY title ASC")
    fun selectTopicByName(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastModified DESC")
    fun selectTopicByLastModified(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastPlayback DESC")
    fun selectTopicByLastPlayback(): Flow<List<Topic>>
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Topic::class], version = 1)
@TypeConverters(Converters::class)
abstract class TopicDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
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
    """.trimIndent()
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
    """.trimIndent()
)
