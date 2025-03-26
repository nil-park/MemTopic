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
    val preferences: Preferences
    fun updatePreferences(prefs: Preferences)
}

class MockSettingsViewModel : ISettingsViewModel {
    override val isInitialized = true
    override val preferences = Preferences()
    override fun updatePreferences(prefs: Preferences) {}
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel(), ISettingsViewModel {

    override var isInitialized by mutableStateOf(false)
        private set

    override var preferences by mutableStateOf(Preferences())
        private set

    override fun updatePreferences(prefs: Preferences) {
        if (isInitialized) {
            preferences = prefs
            viewModelScope.launch {
                repository.setPreferences(prefs)
            }
        }
    }

    init {
        viewModelScope.launch {
            preferences = repository.getPreferences()
            isInitialized = true
        }
    }
}
