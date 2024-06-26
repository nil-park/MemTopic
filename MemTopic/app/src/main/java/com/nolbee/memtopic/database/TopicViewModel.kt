package com.nolbee.memtopic.database

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TopicDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): TopicDatabase {
        return Room.databaseBuilder(app, TopicDatabase::class.java, "topicDatabase").build()
    }

    @Provides
    fun provideTopicDao(db: TopicDatabase): TopicDao {
        return db.topicDao()
    }

    @Provides
    fun provideTopicRepository(topicDao: TopicDao): TopicRepository {
        return TopicRepository(topicDao)
    }
}

interface ITopicViewModel {
    val topics: Flow<List<Topic>>
    var topicToEdit: Topic
    fun upsertTopic(topic: Topic)
    fun deleteTopic(topic: Topic)
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: TopicRepository
) : ViewModel(), ITopicViewModel {
    private val sortType = MutableStateFlow(TopicSortType.LAST_PLAYBACK)

    override val topics: Flow<List<Topic>> = sortType.flatMapLatest { sort ->
        repository.getTopicList(sort)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override var topicToEdit: Topic by mutableStateOf(
        Topic(title = "", content = "", lastModified = Date(), lastPlayback = Date())
    )

    override fun upsertTopic(topic: Topic) {
        viewModelScope.launch {
            repository.upsertTopic(topic)
        }
    }

    override fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }
}

class MockTopicViewModel : ViewModel(), ITopicViewModel {
    private val _topics = MutableStateFlow(
        listOf(sampleTopic00, sampleTopic01)
    )
    override val topics: Flow<List<Topic>> = _topics.asStateFlow()
    override var topicToEdit = Topic(
        title = "", content = "", lastModified = Date(), lastPlayback = Date()
    )

    override fun upsertTopic(topic: Topic) {}
    override fun deleteTopic(topic: Topic) {}
}
