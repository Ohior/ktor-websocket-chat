package com.example.plugins

import com.example.Connection
import io.ktor.routing.*
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.time.*
import io.ktor.application.*
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
//    building a small “echo” service which accepts WebSocket connections, receives
//    text content, and sends it back to the client
    routing {
//        keep track of our Connection objects, and send messages to all connected clients,
//        prefixed with the correct username
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat"){
            println("Adding User")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("You are connected! There are ${connections.count()} users here")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    for (connection in connections) {
                        if (connection.name contentEquals thisConnection.name)continue
                        connection.session.send(textWithUsername)
                    }
//                    connections.forEach {
//                        println("My name id ${it.name}")
//                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection")
                connections -= thisConnection
            }
        }
    }
}
