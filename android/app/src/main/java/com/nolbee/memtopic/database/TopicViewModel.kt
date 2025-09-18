package com.nolbee.memtopic.database

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.utils.ImportResult
import com.nolbee.memtopic.utils.TopicExporter
import com.nolbee.memtopic.utils.TopicImporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportImportState {
    object Idle : ExportImportState()
    object Exporting : ExportImportState()
    object Importing : ExportImportState()
    data class ExportSuccess(val count: Int) : ExportImportState()
    data class ImportSuccess(val count: Int) : ExportImportState()
    data class Error(val message: String) : ExportImportState()
}

interface ITopicViewModel {
    val topics: Flow<List<Topic>>
    var topicToEdit: Topic
    var topicToPlay: Topic
    fun upsertTopic(topic: Topic)
    fun deleteTopic()
    var isDeleteConfirmDialogOpen: Boolean
    var topicToDelete: Topic

    // Export/Import functionality
    val exportImportState: Flow<ExportImportState>
    suspend fun exportTopicsToJson(): String
    suspend fun exportTopicsToUri(uri: Uri): Boolean
    suspend fun importTopicsFromUri(uri: Uri): ImportResult
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: TopicRepository,
    private val topicExporter: TopicExporter,
    private val topicImporter: TopicImporter
) : ViewModel(), ITopicViewModel {
    private val sortType = MutableStateFlow(TopicSortType.LAST_PLAYBACK)
    private val _exportImportState = MutableStateFlow<ExportImportState>(ExportImportState.Idle)

    override val topics: Flow<List<Topic>> = sortType.flatMapLatest { sort ->
        repository.getTopicList(sort)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override val exportImportState: Flow<ExportImportState> = _exportImportState.asStateFlow()

    override var topicToEdit: Topic by mutableStateOf(Topic())
    override var topicToPlay: Topic by mutableStateOf(Topic())

    override fun upsertTopic(topic: Topic) {
        viewModelScope.launch {
            repository.upsertTopic(topic)
        }
    }

    override fun deleteTopic() {
        viewModelScope.launch {
            repository.deleteTopic(topicToDelete)
        }
    }

    override var isDeleteConfirmDialogOpen: Boolean by mutableStateOf(false)
    override var topicToDelete: Topic by mutableStateOf(Topic())

    override suspend fun exportTopicsToJson(): String {
        _exportImportState.value = ExportImportState.Exporting
        return try {
            val allTopics = repository.getAllTopics()
            val jsonData = topicExporter.topicsToJson(allTopics)
            _exportImportState.value = ExportImportState.ExportSuccess(allTopics.size)
            jsonData
        } catch (e: Exception) {
            _exportImportState.value = ExportImportState.Error("내보내기 중 오류가 발생했습니다: ${e.message}")
            throw e
        }
    }

    override suspend fun exportTopicsToUri(uri: Uri): Boolean {
        _exportImportState.value = ExportImportState.Exporting
        return try {
            val allTopics = repository.getAllTopics()
            val jsonData = topicExporter.topicsToJson(allTopics)
            val success = topicExporter.writeJsonToUri(uri, jsonData)

            if (success) {
                _exportImportState.value = ExportImportState.ExportSuccess(allTopics.size)
            } else {
                _exportImportState.value = ExportImportState.Error("파일 저장에 실패했습니다")
            }
            success
        } catch (e: Exception) {
            _exportImportState.value = ExportImportState.Error("내보내기 중 오류가 발생했습니다: ${e.message}")
            false
        }
    }

    override suspend fun importTopicsFromUri(uri: Uri): ImportResult {
        _exportImportState.value = ExportImportState.Importing
        return try {
            val result = topicImporter.importTopicsFromUri(uri)
            when (result) {
                is ImportResult.Success -> {
                    _exportImportState.value = ExportImportState.ImportSuccess(result.importedCount)
                }

                is ImportResult.Error -> {
                    _exportImportState.value = ExportImportState.Error(result.message)
                }
            }
            result
        } catch (e: Exception) {
            val errorMessage = "가져오기 중 오류가 발생했습니다: ${e.message}"
            _exportImportState.value = ExportImportState.Error(errorMessage)
            ImportResult.Error(errorMessage)
        }
    }
}

class MockTopicViewModel : ViewModel(), ITopicViewModel {
    private val _topics = MutableStateFlow(
        listOf(sampleTopic00, sampleTopic01)
    )
    override val topics: Flow<List<Topic>> = _topics.asStateFlow()
    override val exportImportState: Flow<ExportImportState> =
        MutableStateFlow(ExportImportState.Idle)
    override var topicToEdit = Topic()
    override var topicToPlay = Topic()

    override fun upsertTopic(topic: Topic) {}
    override fun deleteTopic() {}
    override var isDeleteConfirmDialogOpen: Boolean = false
    override var topicToDelete = Topic()

    override suspend fun exportTopicsToJson(): String = ""
    override suspend fun exportTopicsToUri(uri: Uri): Boolean = true
    override suspend fun importTopicsFromUri(uri: Uri): ImportResult = ImportResult.Success(0)
}
