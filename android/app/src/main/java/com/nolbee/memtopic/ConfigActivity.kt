package com.nolbee.memtopic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    private lateinit var tbApiKey: EditText
    private lateinit var btnSubmit: Button
    private lateinit var prev: String

    private val TAG = "ConfigTest"

    // TODO: implement dry run option for TTS
    // TODO: add configurations for selecting voice language and varities
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        btnSubmit = findViewById(R.id.btnSubmit)
        tbApiKey = findViewById(R.id.tbApiKey)

        val config = getConfig()
        Log.d(TAG, "apiHost: ${config.textToSpeechApiHost}, apiKey: ${config.textToSpeechApiKey}")
        prev = config.textToSpeechApiKey
        tbApiKey.setText(prev)

        tbApiKey.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                var x = p0.toString()
                btnSubmit.isEnabled = x != prev
            }
        })
    }

    private fun enableFields(e: Boolean) {
        tbApiKey.isEnabled = e
        btnSubmit.isEnabled = e
    }

    fun onClickSubmit(view: View) {
        enableFields(false)
        try {
            val config = getConfig()
            config.textToSpeechApiKey = tbApiKey.text.toString()
            setConfig(config)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d(TAG, "onClickSubmit@ConfigActivity: ${translateErrorMessage(e)}")
            enableFields(true)
        }
        // TODO: display the configuration change is successful
    }

    fun onClickClose(view: View) {
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}