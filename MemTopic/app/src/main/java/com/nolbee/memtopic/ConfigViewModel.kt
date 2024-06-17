package com.nolbee.memtopic

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

open class ConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val secureStore = SecureKeyValueStore(application)
    var gcpTextToSpeechToken by mutableStateOf(loadGcpTextToSpeechToken())
        private set

    var gcpIsTextToSpeechTokenModified by mutableStateOf(false)
        private set

    fun updateGcpTextToSpeechToken(newToken: String) {
        gcpTextToSpeechToken = newToken
        gcpIsTextToSpeechTokenModified = gcpTextToSpeechToken != loadGcpTextToSpeechToken()
    }

    open fun saveGcpTextToSpeechToken() {
        secureStore.set("gcpTextToSpeechToken", gcpTextToSpeechToken)
        gcpIsTextToSpeechTokenModified = false
    }

    protected open fun loadGcpTextToSpeechToken(): String {
        return secureStore.get("gcpTextToSpeechToken") ?: ""
    }
}

open class DummyConfigViewModel : ConfigViewModel(Application()) {
    override fun saveGcpTextToSpeechToken() {}

    override fun loadGcpTextToSpeechToken() = ""
}

