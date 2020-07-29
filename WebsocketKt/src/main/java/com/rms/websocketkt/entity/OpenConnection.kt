package com.rms.websocketkt.entity

data class OpenConnection(
    val code: Int,
    val protocol: String,
    val message: String,
    val url: String,
    val sentRequestAtMillis: Long,
    val receivedResponseAtMillis: Long

)