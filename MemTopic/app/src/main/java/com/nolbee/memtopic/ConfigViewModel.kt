package com.nolbee.memtopic

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


open class ConfigViewModelInterface(application: Application) : AndroidViewModel(application) {
    open var gcpTextToSpeechToken by mutableStateOf("")
        protected set
    open var gcpIsTextToSpeechTokenModified by mutableStateOf(true)
        protected set

    open fun updateGcpTextToSpeechToken(newToken: String) {
        gcpTextToSpeechToken = newToken
    }

    open fun saveGcpTextToSpeechToken() {}
}

class ConfigViewModel(application: Application) : ConfigViewModelInterface(application) {
    private val secureStore: SecureKeyValueStore? = SecureKeyValueStore(application)

    override var gcpTextToSpeechToken by mutableStateOf(loadGcpTextToSpeechToken())

    override var gcpIsTextToSpeechTokenModified by mutableStateOf(false)

    override fun updateGcpTextToSpeechToken(newToken: String) {
        gcpTextToSpeechToken = newToken
        gcpIsTextToSpeechTokenModified = gcpTextToSpeechToken != loadGcpTextToSpeechToken()
    }

    override fun saveGcpTextToSpeechToken() {
        secureStore?.set("gcpTextToSpeechToken", gcpTextToSpeechToken)
        gcpIsTextToSpeechTokenModified = false
    }

    private fun loadGcpTextToSpeechToken(): String {
        return secureStore?.get("gcpTextToSpeechToken") ?: ""
    }
}

class ConfigViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            val viewModel = ConfigViewModel(application)
            return modelClass.cast(viewModel)
                ?: throw IllegalArgumentException("Cannot cast to ConfigViewModel")
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

