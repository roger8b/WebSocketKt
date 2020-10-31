package com.rms.websocketkt.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class WebSocketKtInitializer(
    var url: String? = null,
    var readTimeout: Long? = null,
    var coroutineScope: CoroutineScope? = null,
    var okHttpClient: OkHttpClient? = null,
    var request: Request? = null
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

    val coroutineScope = webSocketInitializer.coroutineScope ?: CoroutineScope(Dispatchers.Main)

    val okHttpClient: OkHttpClient = webSocketInitializer.okHttpClient ?: OkHttpClient.Builder()
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .build()

    val request: Request = webSocketInitializer.request ?: Request.Builder()
        .url(url)
        .build()

    return WebSocketKtImpl.Factory(okHttpClient, request, coroutineScope)
}

