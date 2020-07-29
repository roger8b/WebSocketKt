package com.rms.websocketkt.api

import com.rms.websocketkt.connection.Connection
import com.rms.websocketkt.entity.CloseConnection
import com.rms.websocketkt.entity.ConnectionStatus
import com.rms.websocketkt.entity.Message
import com.rms.websocketkt.entity.OpenConnection
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request

internal const val WEB_SOCKET_KT = "WEB_SOCKET_KT"

internal class WebSocketKtImpl private constructor(
    private val connection: Connection
) : WebSocketKt {

    override fun connect(block: Result<OpenConnection>.() -> Unit) {
        connection.startConnection(block)
    }

    override fun disconnect(block: Result<CloseConnection>.() -> Unit) {
        connection.stopConnection(block)
    }

    override fun sendMessage(message: Message, block: Result<Unit>.() -> Unit) {
        connection.sendMessage(message, block)
    }

    override fun receiveMessage(block: Result<Message>.() -> Unit) {
        connection.observeConnection(block)
    }

    object Factory {
        operator fun invoke(
            client: OkHttpClient,
            request: Request,
            coroutineScope: CoroutineScope
        ): WebSocketKt {
            return WebSocketKtImpl(
                Connection.Factory(
                    client,
                    request,
                    coroutineScope
                )
            )
        }
    }
}