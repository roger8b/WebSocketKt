package com.rms.websocketkt.api

import com.rms.websocketkt.entity.CloseConnection
import com.rms.websocketkt.entity.ConnectionStatus
import com.rms.websocketkt.entity.Message
import com.rms.websocketkt.entity.OpenConnection

interface WebSocketKt {

    fun connect(block: Result<OpenConnection>.() -> Unit)

    fun disconnect(block: Result<CloseConnection>.() -> Unit)

    fun sendMessage(message: Message, block: Result<Unit>.() -> Unit)

    fun receiveMessage(block: Result<Message>.() -> Unit)

}