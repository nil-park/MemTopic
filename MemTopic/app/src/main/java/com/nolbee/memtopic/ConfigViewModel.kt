package com.nolbee.memtopic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

open class ConfigViewModel : ViewModel() {
    var gcpTextToSpeechToken by mutableStateOf("")
        private set

    fun updateGcpTextToSpeechToken(newToken: String) {
        gcpTextToSpeechToken = newToken
    }
}
