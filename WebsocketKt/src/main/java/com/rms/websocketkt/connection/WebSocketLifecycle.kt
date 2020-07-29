package com.rms.websocketkt.connection

import android.util.Log
import com.coroutines.flowext.FlowEmitter
import com.coroutines.flowext.FlowPublisher
import com.rms.websocketkt.api.WEB_SOCKET_KT
import com.rms.websocketkt.entity.ConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

internal class WebSocketLifecycle private constructor(
    private val scope: CoroutineScope,
    private val emitter: FlowEmitter<ConnectionStatus>
) : WebSocketListener() {

    private val publisher: FlowPublisher<ConnectionStatus> by lazy {
        emitter.asPublisher()
    }

    fun loadPublisher(): FlowPublisher<ConnectionStatus> = publisher


    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(WEB_SOCKET_KT, "[onClosed] $code $reason $webSocket")
        emit(ConnectionStatus.OnClosed(webSocket, code, reason))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(WEB_SOCKET_KT, "[onClosing] $code $reason $webSocket")
        emit(ConnectionStatus.OnClosed(webSocket, code, reason))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(WEB_SOCKET_KT, "[onFailure] $response ${t.message} $webSocket", t)
        emit(ConnectionStatus.OnFailure(webSocket, t, response))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(WEB_SOCKET_KT, "[onMessage] $text $webSocket")
        emit(ConnectionStatus.OnMessageString(webSocket, text))
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(WEB_SOCKET_KT, "[onMessage] ${bytes.toByteArray()} $webSocket")
        emit(ConnectionStatus.OnMessageByteString(webSocket, bytes))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(WEB_SOCKET_KT, "[onOpen] $response $webSocket")
        emit(ConnectionStatus.OnOpen(webSocket, response))
    }

    private fun emit(connectionStatus: ConnectionStatus) {
        scope.launch {
            emitter.emit(connectionStatus)
        }
    }

    object Factory {
        operator fun invoke(emitter: FlowEmitter<ConnectionStatus>, scope: CoroutineScope) =
            WebSocketLifecycle(scope, emitter)
    }
}