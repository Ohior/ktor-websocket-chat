package com.example

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object{
//        AtomicInteger is a thread-safe data structure for the counter. This ensures that two users
//        will never receive the same ID for their username – even when their two Connection objects
//        are created simultaneously on separate threads
        val lastId = AtomicInteger(0)
    }
    val name = "user ${lastId.getAndIncrement()}"
}