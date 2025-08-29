package com.nolbee.memtopic.account_view

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface IAccountViewModel {
    var gcpTextToSpeechToken: String
    var gcpIsTextToSpeechTokenModified: Boolean
    fun updateGcpTextToSpeechToken(newToken: String)
    fun saveGcpTextToSpeechToken()

}

@HiltViewModel
class AccountViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), IAccountViewModel {
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
        try {
            secureStore?.get("gcpTextToSpeechToken")?.let {
                return it
            }
        } catch (e: Exception) {
            secureStore?.set("gcpTextToSpeechToken", "")
        }
        return ""
    }
}

class MockAccountViewModel() : IAccountViewModel {
    override var gcpTextToSpeechToken = "This is sample token"
    override var gcpIsTextToSpeechTokenModified = true
    override fun updateGcpTextToSpeechToken(newToken: String) {}
    override fun saveGcpTextToSpeechToken() {}
}
