package com.rms.websocketkt.connection

import com.rms.websocketkt.api.WebsocketKt
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class WebSocketKtInitializer(
    var url: String? = null,
    var readTimeout: Long? = null
)


fun webSocketKtInitializer(init: WebSocketKtInitializer.() -> Unit): WebsocketKt {
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

    return WebSocketKtImpl.Factory(client, request)
}

