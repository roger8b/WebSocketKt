package com.rms.websocketkt.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class WebSocketKtInitializer(
    var url: String? = null,
    var readTimeout: Long? = null
)


fun webSocketKtInitializer(init: WebSocketKtInitializer.() -> Unit): WebSocketKt {
    val webSocketInitializer = WebSocketKtInitializer()
    webSocketInitializer.init()

    val url = checkNotNull(webSocketInitializer.url) {
        "[webSocketKtInitializer] url must be set "
    }
    val readTimeout = checkNotNull(webSocketInitializer.readTimeout) {
        "[webSocketKtInitializer] readTimeout must be set "
    }

    val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .build()
    val request: Request = Request.Builder()
        .url(url)
        .build()

    return WebSocketKtImpl.Factory(client, request, CoroutineScope(Dispatchers.Main))
}

