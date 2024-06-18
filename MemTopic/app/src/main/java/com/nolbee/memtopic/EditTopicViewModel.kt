package com.nolbee.memtopic

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

open class EditTopicViewModel(application: Application) : AndroidViewModel(application) {
    var topicTitle by mutableStateOf("")
        private set

    var topicContent by mutableStateOf("")
        private set

    var isModified by mutableStateOf(true)
        private set

    var isNew by mutableStateOf(true)
        private set

    fun updateTitle(newTitle: String) {
        topicTitle = newTitle
    }

    fun updateContent(newContent: String) {
        topicContent = newContent
    }

    fun prepareAddNewTopic() {
        topicTitle = ""
        topicContent = ""
        isNew = true
        isModified = false
    }
}
