package com.rms.websocketkt.connection

import com.rms.websocketkt.api.WebsocketKt
import com.rms.websocketkt.entity.ConnectionStatus
import com.rms.websocketkt.entity.Message
import okhttp3.OkHttpClient
import okhttp3.Request

internal class WebsocketKtImpl(
    val client: OkHttpClient,
    val request: Request
) : WebsocketKt {

    override fun connect(block: Result<ConnectionStatus>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun disconnect(block: Result<ConnectionStatus>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: Message, block: Result<Unit>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun receiveMessage(block: Result<Message>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getStatus(block: Result<ConnectionStatus>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun reconnect(block: Result<ConnectionStatus>.() -> Unit) {
        TODO("Not yet implemented")
    }
}