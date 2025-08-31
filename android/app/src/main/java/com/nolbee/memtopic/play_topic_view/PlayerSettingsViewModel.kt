package com.nolbee.memtopic.play_topic_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.database.SettingsRepository
import com.nolbee.memtopic.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IPlayerSettingsViewModel {
    val isInitialized: Boolean
    val settings: Settings
    fun updateSettings(settings: Settings)
}

class MockPlayerSettingsViewModel : IPlayerSettingsViewModel {
    override val isInitialized = true
    override val settings = Settings()
    override fun updateSettings(settings: Settings) {}
}

@HiltViewModel
class PlayerSettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel(), IPlayerSettingsViewModel {

    override var isInitialized by mutableStateOf(false)
        private set

    override var settings by mutableStateOf(Settings())
        private set

    override fun updateSettings(settings: Settings) {
        if (isInitialized) {
            this.settings = settings
            viewModelScope.launch {
                repository.setSettings(settings)
            }
        }
    }

    init {
        viewModelScope.launch {
            settings = repository.getSettings()
            isInitialized = true
        }
    }
}
