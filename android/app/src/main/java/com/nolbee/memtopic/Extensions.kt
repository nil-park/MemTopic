package com.nolbee.memtopic

import android.content.Context
import android.util.Log
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import java.io.File

const val LOCAL_DIR_FOR_TOPICS = "topics"

private const val TAG = "Extensions"

fun Context.saveTopic(name: String, content: String): String {
    val dir = File(filesDir, LOCAL_DIR_FOR_TOPICS)
    if (!dir.isDirectory) {
        if (dir.mkdir()) {
            Log.d(TAG, "Created dir to contain topic files: ${dir.absolutePath}.")
        } else {
            Log.d(TAG, "Failed creating dir ${dir.absolutePath}.")
        }
    }
    val currentTopicFile = File(dir, "$name.txt")
    return if (currentTopicFile.exists()) {
        "The topic name $name already exists."
    }
    else {
        currentTopicFile.writeText(content)
        "okay"
    }
}

fun Context.translateErrorMessage(e: java.lang.Exception): String {
    var msg = e.message
    if (e is ConnectTimeoutException
        || e is SocketTimeoutException
        || e is HttpRequestTimeoutException
    ) {
        msg = resources.getString(R.string.timeout)
    }
    if (msg.isNullOrEmpty()) {
        msg = e.toString()
    }
    return msg
}
