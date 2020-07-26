package com.rms.websocketkt.api

import com.rms.websocketkt.entity.ConnectionStatus
import com.rms.websocketkt.entity.Message

interface WebsocketKt {

    fun connect(block: Result<ConnectionStatus>.() -> Unit)

    fun disconnect(block: Result<ConnectionStatus>.() -> Unit)

    fun sendMessage(message: Message, block: Result<Unit>.() -> Unit)

    fun receiveMessage(block: Result<Message>.() -> Unit)

    fun getStatus(block: Result<ConnectionStatus>.() -> Unit)

    fun reconnect(block: Result<ConnectionStatus>.() ->Unit)

}