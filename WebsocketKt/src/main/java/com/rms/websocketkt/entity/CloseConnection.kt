package com.rms.websocketkt.entity

data class CloseConnection(
    val code: Int,
    val reason: String
)