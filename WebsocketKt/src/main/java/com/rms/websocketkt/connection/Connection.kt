package com.rms.websocketkt.connection

import android.util.Log
import com.coroutines.flowext.FlowEmitter
import com.coroutines.flowext.flowSubscriberOf
import com.rms.websocketkt.api.WEB_SOCKET_KT
import com.rms.websocketkt.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

internal const val NORMAL_CLOSURE: Int = 1000

internal class Connection private constructor(
    private val client: OkHttpClient,
    private val request: Request,
    private val coroutineScope: CoroutineScope,
    private val lifecycle: WebSocketLifecycle
) {

    private var webSocket: WebSocket? = null

    fun startConnection(result: Result<OpenConnection>.() -> Unit) {
        webSocket = client.newWebSocket(request, lifecycle)
        client.dispatcher.executorService.shutdown()

        publisherOpen()
        coroutineScope.launch {
            getConnectionStatusFlow().collect { connectionStatus ->
                when (connectionStatus) {
                    is ConnectionStatus.OnOpen -> result(
                        Result.success(
                            mapOpenConnection(
                                connectionStatus.response
                            )
                        )
                    )
                    is ConnectionStatus.OnFailure -> result(
                        Result.failure(
                            WebSocketKtErrors.StartConnectionError(connectionStatus.t.message.orEmpty())
                        )
                    )
                }
            }
        }
    }

    private fun mapOpenConnection(response: okhttp3.Response): OpenConnection =
        OpenConnection(
            code = response.code,
            protocol = response.protocol.name,
            message = response.message,
            url = response.request.url.toString(),
            sentRequestAtMillis = response.sentRequestAtMillis,
            receivedResponseAtMillis = response.receivedResponseAtMillis
        )

    fun stopConnection(result: Result<CloseConnection>.() -> Unit) {
        getWebSocket().close(NORMAL_CLOSURE, null)

        coroutineScope.launch() {
            getConnectionStatusFlow().collect { connectionStatus ->
                when (connectionStatus) {
                    is ConnectionStatus.OnClosed -> {
                        result(
                            Result.success(
                                mapCloseConnection(connectionStatus.code, connectionStatus.reason)
                            )
                        )
                    }
                    is ConnectionStatus.OnFailure -> result(
                        Result.failure(
                            WebSocketKtErrors.CloseConnectionError(connectionStatus.t.message.orEmpty())
                        )
                    )
                }
            }
        }
    }

    private fun mapCloseConnection(code: Int, reason: String): CloseConnection =
        CloseConnection(code, reason)

    fun sendMessage(
        message: Message,
        block: Result<Unit>.() -> Unit
    ) {
        Log.d(WEB_SOCKET_KT, "[sendMessage] $message")
        val result = when (message) {
            is Message.Text -> getWebSocket().send(message.value)
            is Message.Bytes -> getWebSocket().send(message.value)
        }

        if (result) {
            block(Result.success(Unit))
        } else {
            block(
                Result.failure(
                    WebSocketKtErrors.ErrorOnSendMessage("[sendMessage] error on send message $message")
                )
            )
        }
    }

    fun observeConnection(block: Result<Message>.() -> Unit) {
        coroutineScope.launch {
            getConnectionStatusFlow().filter { it is ConnectionStatus.OnMessageString }
                .collect { connectionStatus ->
                    connectionStatus as ConnectionStatus.OnMessageString
                    block(Result.success(Message.Text(connectionStatus.text)))
                }
        }
    }

    private fun getConnectionStatusFlow() =
        flowSubscriberOf<ConnectionStatus>(lifecycle.loadPublisher()).asFlow()

    private fun publisherOpen() {
        coroutineScope.launch {
            lifecycle.loadPublisher().open()
        }
    }

    private fun getWebSocket(): WebSocket =
        checkNotNull(webSocket) { "[webSocket] not initialized" }

    object Factory {
        operator fun invoke(
            client: OkHttpClient,
            request: Request,
            coroutineScope: CoroutineScope
        ): Connection {
            return Connection(
                client,
                request,
                coroutineScope,
                WebSocketLifecycle.Factory(
                    FlowEmitter(),
                    coroutineScope
                )
            )
        }
    }
}