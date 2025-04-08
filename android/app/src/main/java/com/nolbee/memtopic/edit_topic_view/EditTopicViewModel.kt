package com.nolbee.memtopic.edit_topic_view

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.account_view.SecureKeyValueStore
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.Topic
import kotlinx.coroutines.launch
import java.util.Date

class EditTopicViewModel : ViewModel() {
    var topicRef: Topic by mutableStateOf(
        Topic(title = "", content = "", lastModified = Date(), lastPlayback = Date())
    )
    var topicTitle by mutableStateOf(topicRef.title)
        private set

    var topicContent by mutableStateOf(topicRef.content)
        private set

    var isSavable by mutableStateOf(false)
        private set

    var isNew by mutableStateOf(false)
        private set

    fun updateTitle(newTitle: String) {
        topicTitle = newTitle
        val isModified = (topicTitle != topicRef.title) || (topicContent != topicRef.content)
        isSavable = isModified && topicTitle.trim() != "" && topicContent.trim() != ""
    }

    fun updateContent(newContent: String) {
        topicContent = newContent
        val isModified = (topicTitle != topicRef.title) || (topicContent != topicRef.content)
        isSavable = isModified && topicTitle.trim() != "" && topicContent.trim() != ""
    }

    fun setTopicReference(topic: Topic) {
        topicTitle = topic.title
        topicContent = topic.content
        topicRef = topic
        isNew = topic.id == 0
    }

    var isConfirmDialogOpen by mutableStateOf(false)

    var openBottomSheet by mutableStateOf(false)
    var selectedLanguageCode by mutableStateOf("en-US")
    var selectedVoiceCode by mutableStateOf("en-US-Neural2-J")
    var languageCodes by mutableStateOf(listOf<String>())
    var voiceCodes by mutableStateOf(listOf<String>())

    fun loadLanguageCodes(context: Context) {
        viewModelScope.launch {
            val apiKey = SecureKeyValueStore(context).get("gcpTextToSpeechToken") ?: ""
            val client = TextToSpeechGCP(apiKey)
            languageCodes = client.listLanguageCodes()
        }
    }

    fun loadVoiceCodes(context: Context) {
        viewModelScope.launch {
            val apiKey = SecureKeyValueStore(context).get("gcpTextToSpeechToken") ?: ""
            val client = TextToSpeechGCP(apiKey)
            voiceCodes = client.listVoices(selectedLanguageCode).map { it.name }
        }
    }
}
