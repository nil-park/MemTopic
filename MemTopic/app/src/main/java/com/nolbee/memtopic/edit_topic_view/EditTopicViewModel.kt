package com.nolbee.memtopic.edit_topic_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nolbee.memtopic.database.Topic
import java.util.Date

class EditTopicViewModel : ViewModel() {
    var topicRef: Topic by mutableStateOf(
        Topic(title = "", content = "", lastModified = Date(), lastPlayback = Date())
    )
    var topicTitle by mutableStateOf(topicRef.title)
        private set

    var topicContent by mutableStateOf(topicRef.content)
        private set

    var isModified by mutableStateOf(false)
        private set

    var isNew by mutableStateOf(false)
        private set

    fun updateTitle(newTitle: String) {
        topicTitle = newTitle
        isModified = (topicTitle != topicRef.title) || (topicContent != topicRef.content)
    }

    fun updateContent(newContent: String) {
        topicContent = newContent
        isModified = (topicTitle != topicRef.title) || (topicContent != topicRef.content)
    }

    fun setTopicReference(topic: Topic) {
        topicTitle = topic.title
        topicContent = topic.content
        topicRef = topic
        isNew = topic.id == 0
    }

    var isOpenConfirmDialog by mutableStateOf(false)
}
