package com.sdzshn3.android.websocketpractice

import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface ChatService {

    @Receive
    fun observeWebSocketEvent(): Flow<WebSocket.Event>

    @Receive
    fun observeIncomingMessage(): Flow<String>

    @Send
    fun sendMessage(message: String)
}