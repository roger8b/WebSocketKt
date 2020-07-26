package com.rms.websocketkt.entity

import okio.ByteString

sealed class Message {

    data class Text(val value: String) : Message()
    data class Bytes(val value: ByteString) : Message()
}