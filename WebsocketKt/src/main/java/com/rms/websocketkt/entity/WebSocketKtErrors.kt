package com.rms.websocketkt.entity

sealed class WebSocketKtErrors : Throwable() {

    data class StartConnectionError(override val message: String): WebSocketKtErrors()
    data class CloseConnectionError(override val message: String): WebSocketKtErrors()
    data class ErrorOnSendMessage(override val message: String): WebSocketKtErrors()
}