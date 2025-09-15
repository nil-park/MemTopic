package com.nolbee.memtopic.database

import kotlinx.coroutines.flow.Flow

enum class TopicSortType {
    TITLE,
    LAST_MODIFIED,
    LAST_PLAYBACK
}

interface ITopicRepository {
    suspend fun upsertTopic(topic: Topic)
    suspend fun deleteTopic(topic: Topic)
    suspend fun getTopic(id: Int): Topic?
    fun getTopicList(sortType: TopicSortType): Flow<List<Topic>>
    suspend fun getAllTopics(): List<Topic>
}

class TopicRepository(private val topicDao: TopicDao) : ITopicRepository {
    override suspend fun upsertTopic(topic: Topic) {
        topicDao.upsertTopic(topic)
    }

    override suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
    }

    override suspend fun getTopic(id: Int): Topic? {
        return topicDao.getTopic(id)
    }

    override fun getTopicList(sortType: TopicSortType): Flow<List<Topic>> {
        return when (sortType) {
            TopicSortType.TITLE -> topicDao.selectTopicByName()
            TopicSortType.LAST_MODIFIED -> topicDao.selectTopicByLastModified()
            TopicSortType.LAST_PLAYBACK -> topicDao.selectTopicByLastPlayback()
        }
    }

    override suspend fun getAllTopics(): List<Topic> {
        return topicDao.getAllTopics()
    }
}

class MockTopicRepository : ITopicRepository {
    override suspend fun upsertTopic(topic: Topic) {}
    override suspend fun deleteTopic(topic: Topic) {}
    override suspend fun getTopic(id: Int): Topic? = null
    override fun getTopicList(sortType: TopicSortType): Flow<List<Topic>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override suspend fun getAllTopics(): List<Topic> = emptyList()
}
