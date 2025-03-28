package com.nolbee.memtopic.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.database.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ISettingsViewModel {
    val isInitialized: Boolean
    val settings: Settings
    fun updateSettings(settings: Settings)
}

class MockSettingsViewModel : ISettingsViewModel {
    override val isInitialized = true
    override val settings = Settings()
    override fun updateSettings(settings: Settings) {}
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel(), ISettingsViewModel {

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
