package com.rms.websocketkt.playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.rms.websocketkt.api.WebSocketKt
import com.rms.websocketkt.api.webSocketKtInitializer
import com.rms.websocketkt.entity.Message
import kotlinx.android.synthetic.main.activity_main.*

const val WEB_SOCKET_TEST_URL = "ws://echo.websocket.org"

class MainActivity : AppCompatActivity() {

    var counter = 0

    private val list: ArrayList<String> by lazy {
        arrayListOf<String>()
    }

    private val arrayAdapterView: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
    }

    private var webSocketKt: WebSocketKt? = null

    private fun createConnection(wsUrl: String) {
        webSocketKt = webSocketKtInitializer {
            url = wsUrl
            readTimeout = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupButton()
        setupList()
        testSetup()
    }

    private fun testSetup() {
        val s = "Mensagem $counter"
        etMessage.setText(s)
        etWebSocketAddress.setText(WEB_SOCKET_TEST_URL)
    }

    private fun updateSendMessage(message: String) {
        etMessage.setText(message)
        counter++
    }

    private fun setupList() {
        lvReceivedMessages.adapter = arrayAdapterView
    }

    private fun setupButton() {
        btConnect.setOnClickListener {
            val wsUrl = etWebSocketAddress.text.toString()
            createConnection(wsUrl)

            webSocketKt?.run {
                connect {
                    onSuccess {
                        postMessage(it.url, MessageType.CONNECTED)
                        btConnect.isActivated = false
                        observeMessages()
                    }
                    onFailure {
                        postMessage(it.message.orEmpty(), MessageType.ERROR)
                    }
                }
            }
        }

        btDisconnect.setOnClickListener {
            webSocketKt?.run {
                disconnect {
                    onSuccess {
                        postMessage("${it.code} ${it.reason}", MessageType.DISCONNECTED)
                    }

                    onFailure {
                        postMessage(it.message.orEmpty(), MessageType.ERROR)
                    }
                }
            }
        }

        btSend.setOnClickListener {
            val message = "Mensagem $counter"

            webSocketKt?.run {
                sendMessage(Message.Text(message)) {
                    onSuccess {
                        postMessage(message, MessageType.SEND)
                        updateSendMessage(message)
                    }
                    onFailure {
                        postMessage(it.message.orEmpty(), MessageType.ERROR)
                    }
                }
            }
        }
    }

    private fun observeMessages() {
        webSocketKt?.run {
            receiveMessage {
                onSuccess {
                    if (it is Message.Text) {
                        postMessage(it.value, MessageType.RECEIVE)
                    }
                }
            }
        }
    }

    private fun postMessage(message: String, type: MessageType) {
        list.add(
            when (type) {
                MessageType.CONNECTED -> "Connected!!! $message"
                MessageType.DISCONNECTED -> "Disconnected!!! $message"
                MessageType.RECEIVE -> "<-- $message"
                MessageType.SEND -> "--> $message"
                MessageType.ERROR -> "Error!!! $message"
            }
        )
        arrayAdapterView.notifyDataSetChanged()
    }
}

enum class MessageType {
    CONNECTED,
    DISCONNECTED,
    RECEIVE,
    SEND,
    ERROR
}