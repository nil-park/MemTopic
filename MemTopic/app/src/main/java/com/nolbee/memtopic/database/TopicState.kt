package com.nolbee.memtopic.database

import java.util.Date

data class TopicState(
    val topics: List<Topic> = emptyList(),
    val name: String = "",
    val lastModified: Date = Date(),
    val lastPlayback: Date = Date(),
    val isAddingTopic: Boolean = false,
    val sortType: TopicSortType = TopicSortType.LAST_PLAYBACK
)
