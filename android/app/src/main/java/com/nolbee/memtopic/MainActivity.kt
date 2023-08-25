package com.nolbee.memtopic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var tbScript: EditText
    private lateinit var btnSubmit: Button

    private val TAG = "GCPTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tbScript = findViewById(R.id.tbScript)
        btnSubmit = findViewById(R.id.btnSubmit)

        tbScript.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                btnSubmit.isEnabled = p0.toString().isNotEmpty()
            }
        })
    }

    private fun enableFields(e: Boolean) {
        tbScript.isEnabled = e
        btnSubmit.isEnabled = e
    }

    fun onClickSubmit(view: View) {
        enableFields(false)
        val script = tbScript.text.toString()
        Log.d(TAG, "onClickSubmit: $script")
        enableFields(true)
    }
}