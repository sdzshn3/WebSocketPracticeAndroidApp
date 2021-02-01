package com.sdzshn3.android.websocketpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sdzshn3.android.websocketpractice.databinding.ActivityMainBinding
import com.tinder.scarlet.Message
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messages = ArrayList<com.sdzshn3.android.websocketpractice.Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")!!
        val adapter = Adapter(username)

        binding.recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = false
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        val okHttpClient = OkHttpClient.Builder().build()
        val scarlet = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory("http://192.168.0.132:8080/chat"))
            .backoffStrategy(LinearBackoffStrategy(1000))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
            .lifecycle(AndroidLifecycle.ofServiceStarted(application, this))
            .build()

        val chatService = scarlet.create<ChatService>()

        lifecycleScope.launchWhenCreated {
            chatService.observeWebSocketEvent().collect {
                println(it)
                when (it) {
                    is WebSocket.Event.OnConnectionClosed -> {

                    }
                    is WebSocket.Event.OnConnectionClosing -> {

                    }
                    is WebSocket.Event.OnConnectionFailed -> {

                    }
                    is WebSocket.Event.OnConnectionOpened<Any> -> {
                        val outGo = OutGo(
                            TYPE_NEW_SESSION,
                            username,
                            ""
                        )
                        val outGoJson: String = Gson().toJson(outGo)
                        chatService.sendMessage(outGoJson)
                    }
                    is WebSocket.Event.OnMessageReceived -> {
                        val inComeJson: String = (it.message as Message.Text).value
                        println(inComeJson)
                        val inCome: InCome = Gson().fromJson(inComeJson, InCome::class.java)
                        println(inCome)
                        if (inCome.userName != username) {
                            var scrollToBottom = false
                            if (layoutManager.findLastVisibleItemPosition() == messages.size - 1) {
                                scrollToBottom = true
                            }
                            messages.add(
                                Message(
                                    UUID.randomUUID(),
                                    inCome.message,
                                    inCome.userName
                                )
                            )
                            val newList = ArrayList<com.sdzshn3.android.websocketpractice.Message>()
                            newList.addAll(messages)
                            adapter.submitList(newList)
                            if (scrollToBottom) {
                                binding.recyclerView.smoothScrollToPosition(newList.size - 1)
                            }
                        } else if (inCome.userName == username && inCome.type == TYPE_NEW_SESSION) {
                            var scrollToBottom = false
                            if (layoutManager.findLastVisibleItemPosition() == messages.size - 1) {
                                scrollToBottom = true
                            }
                            messages.add(
                                Message(
                                    UUID.randomUUID(),
                                    inCome.message,
                                    inCome.userName
                                )
                            )
                            val newList = ArrayList<com.sdzshn3.android.websocketpractice.Message>()
                            newList.addAll(messages)
                            adapter.submitList(newList)
                            if (scrollToBottom) {
                                binding.recyclerView.smoothScrollToPosition(newList.size - 1)
                            }
                        }
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            if (message.isBlank()) {
                Toast.makeText(this, "message is blank", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.editText.setText("")
            var scrollToBottom = false
            if (layoutManager.findLastVisibleItemPosition() == messages.size - 1) {
                scrollToBottom = true
            }
            messages.add(
                Message(
                    UUID.randomUUID(),
                    message,
                    username
                )
            )
            val newList = ArrayList<com.sdzshn3.android.websocketpractice.Message>()
            newList.addAll(messages)
            adapter.submitList(newList)

            if (scrollToBottom) {
                binding.recyclerView.smoothScrollToPosition(newList.size - 1)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val outGo = OutGo(
                    TYPE_MESSAGE,
                    username,
                    message
                )
                val outGoJson = Gson().toJson(outGo)
                chatService.sendMessage(outGoJson)
            }
        }
    }
}