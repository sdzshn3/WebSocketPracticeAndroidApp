package com.sdzshn3.android.websocketpractice

import java.util.*

data class Message(
    val time: UUID,
    val message: String,
    val sender: String
)
