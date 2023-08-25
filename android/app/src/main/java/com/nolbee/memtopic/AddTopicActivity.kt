package com.nolbee.memtopic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddTopicActivity : AppCompatActivity() {

    private val TAG = "GCPTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)
    }

    fun onClickSubmit(view: View) {
        Log.d(TAG, "onClickSubmitCreatingTopic")
        val nameView = findViewById<EditText>(R.id.tbTopicName)
        val contentView = findViewById<EditText>(R.id.tbTopicContent)
        val status = saveTopic(nameView.text.toString(), contentView.text.toString())
        if (status != "okay") {
            Log.d(TAG, status)
            onError(status)
        } else {
            finish()
        }
    }

    private fun onError(msg: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.common_error)
            .setMessage(msg)
            .setNeutralButton(R.string.common_accept) { _, _ ->

            }
            .show()
    }
}
