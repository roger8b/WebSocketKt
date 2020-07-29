package com.rms.websocketkt.entity

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

internal sealed class ConnectionStatus {

    data class OnOpen(
        val webSocket: WebSocket,
        val response: Response
    ) : ConnectionStatus()

    data class OnClosing(
        val webSocket: WebSocket,
        val code: Int,
        val reason: String
    ) : ConnectionStatus()

    data class OnClosed(
        val webSocket: WebSocket,
        val code: Int,
        val reason: String
    ) : ConnectionStatus()

    data class OnFailure(
        val webSocket: WebSocket,
        val t: Throwable,
        val response: Response?
    ) : ConnectionStatus()

    data class OnMessageString(
        val webSocket: WebSocket,
        val text: String
    ) : ConnectionStatus()

    data class OnMessageByteString(
        val webSocket: WebSocket,
        val byte: ByteString
    ) : ConnectionStatus()
}