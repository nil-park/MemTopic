package com.nolbee.memtopic.database

import kotlinx.coroutines.flow.Flow

enum class TopicSortType {
    TITLE,
    LAST_MODIFIED,
    LAST_PLAYBACK
}

class TopicRepository(private val topicDao: TopicDao) {
    suspend fun upsertTopic(topic: Topic) {
        topicDao.upsertTopic(topic)
    }

    suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
    }

    fun getTopicList(sortType: TopicSortType): Flow<List<Topic>> {
        return when (sortType) {
            TopicSortType.TITLE -> topicDao.selectTopicByName()
            TopicSortType.LAST_MODIFIED -> topicDao.selectTopicByLastModified()
            TopicSortType.LAST_PLAYBACK -> topicDao.selectTopicByLastPlayback()
        }
    }
}
