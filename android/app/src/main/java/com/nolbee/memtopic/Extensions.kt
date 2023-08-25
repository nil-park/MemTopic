package com.nolbee.memtopic

import android.content.Context
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*

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
