package com.nolbee.memtopic.edit_topic_view

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.R
import com.nolbee.memtopic.account_view.SecureKeyValueStore
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTopicViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val defaultLanguageCode = context.getString(R.string.default_gcp_language_code)
    private val defaultVoiceType = context.getString(R.string.default_gcp_voice_name)

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
        val voiceOptions = encodeVoiceOptionsToJson()
        val isModified =
            (topicTitle != topicRef.title) || (topicContent != topicRef.content) || (voiceOptions != topicRef.options)
        isSavable =
            isModified && topicTitle.trim() != "" && topicContent.trim() != "" && selectedVoiceType != ""
    }

    fun updateContent(newContent: String) {
        topicContent = newContent
        val voiceOptions = encodeVoiceOptionsToJson()
        val isModified =
            (topicTitle != topicRef.title) || (topicContent != topicRef.content) || (voiceOptions != topicRef.options)
        isSavable =
            isModified && topicTitle.trim() != "" && topicContent.trim() != "" && selectedVoiceType != ""
    }

    fun setTopicReference(topic: Topic) {
        topicTitle = topic.title
        topicContent = topic.content
        topicRef = topic
        val voiceOptions = JSONObject(topic.options)
        selectedLanguageCode = voiceOptions.optString("languageCode", defaultLanguageCode)
        selectedVoiceType = voiceOptions.optString("voiceType", defaultLanguageCode)
        Log.d("EditTopicViewModel", "setTopicReference: $topic")
        isNew = topic.id == 0
    }

    var isConfirmDialogOpen by mutableStateOf(false)

    var openBottomSheet by mutableStateOf(false)
    var selectedLanguageCode by mutableStateOf(defaultLanguageCode)
    var selectedVoiceType by mutableStateOf(defaultVoiceType)
    var languageCodes by mutableStateOf(listOf<String>())
    var voiceTypes by mutableStateOf(listOf<String>())

    fun updateVoiceType(languageCode: String, voiceType: String) {
        selectedLanguageCode = languageCode
        selectedVoiceType = voiceType
        val voiceOptions = encodeVoiceOptionsToJson()
        val isModified =
            (topicTitle != topicRef.title) || (topicContent != topicRef.content) || (voiceOptions != topicRef.options)
        isSavable =
            isModified && topicTitle.trim() != "" && topicContent.trim() != "" && selectedVoiceType != ""
    }

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
            voiceTypes = client.listVoices(selectedLanguageCode).map { it.name }
        }
    }

    fun encodeVoiceOptionsToJson(): String {
        return """{"languageCode":"$selectedLanguageCode","voiceType":"$selectedVoiceType"}"""
    } // TODO: remove redunant code (TopicListView)
}

