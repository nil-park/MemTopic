package com.nolbee.memtopic

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var tbScript: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnConfig: ImageButton

    private val TAG = "GCPTest"

    private val player = MediaPlayer()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tbScript = findViewById(R.id.tbScript)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnConfig = findViewById(R.id.btnConfig)

        tbScript.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                btnSubmit.isEnabled = p0.toString().isNotEmpty()
            }
        })

        tbScript.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (btnSubmit.isEnabled) btnSubmit.performClick()
                Log.d(TAG, "ENTER!")
            }
            false
        })

        player.setOnCompletionListener {
            enableFields(true)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val config = getConfig()
            Log.d(TAG, "apiHost: ${config.textToSpeechApiHost}, apiKey: ${config.textToSpeechApiKey}")
        }
    }

    private fun enableFields(e: Boolean) {
        tbScript.isEnabled = e
        btnSubmit.isEnabled = e
        btnConfig.isEnabled = e
    }

    fun onClickSubmit(view: View) {
        enableFields(false)
        val script = tbScript.text.toString()
        Log.d(TAG, "onClickSubmit: $script")
        synthesize(script)
    }

    fun onClickAddTopic(view: View) {
        Log.d(TAG, "onClickAddTopic")
        val intent = Intent(this, AddTopicActivity::class.java)
        resultLauncher.launch(intent)
    }

    fun onClickConfig(view: View) {
        Log.d(TAG, "onClickConfig")
        val intent = Intent(this, ConfigActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun play(audioBase64: String) {
        try {
            player.reset()
            player.setDataSource("data:audio/mp3;base64,$audioBase64")
            player.prepare()
            player.start()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "Error from play(): ${e.message}")
            enableFields(true)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun synthesize(text: String) {
        val config = getConfig()
        // TODO: add configuration for selecting TTS engine providers: AWS, Azure...
        val client = TextToSpeechGCP(config.textToSpeechApiKey, "en-US", "en-US-Neural2-J")
        GlobalScope.launch {
            try {
                val audioBase64 = client.Synthesize(text)
                withContext(Dispatchers.Main) {
                    play(audioBase64)
                }
            } catch (e: java.lang.Exception) {
                val msg = translateErrorMessage(e)
                Log.d(TAG, "Error from synthesize(): $msg")
            } finally {
                withContext(Dispatchers.Main) {
                    enableFields(true)
                }
            }
        }
    }
}