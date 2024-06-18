package com.nolbee.memtopic

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class ConfigViewModel(private val application: Application) : AndroidViewModel(application) {
    private var secureStore: SecureKeyValueStore? = null

    fun initSecureStore() {
        secureStore = SecureKeyValueStore(application)
        gcpTextToSpeechToken = loadGcpTextToSpeechToken()
    }

    var gcpTextToSpeechToken by mutableStateOf("")
        private set

    var gcpIsTextToSpeechTokenModified by mutableStateOf(false)
        private set

    fun updateGcpTextToSpeechToken(newToken: String) {
        gcpTextToSpeechToken = newToken
        gcpIsTextToSpeechTokenModified = gcpTextToSpeechToken != loadGcpTextToSpeechToken()
    }

    open fun saveGcpTextToSpeechToken() {
        secureStore?.set("gcpTextToSpeechToken", gcpTextToSpeechToken)
        gcpIsTextToSpeechTokenModified = false
    }

    private fun loadGcpTextToSpeechToken(): String {
        return secureStore?.get("gcpTextToSpeechToken") ?: ""
    }
}
