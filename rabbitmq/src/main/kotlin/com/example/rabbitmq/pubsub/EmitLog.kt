package com.example.rabbitmq.pubsub

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmitLog

fun main(args: Array<String>) {

    val exchangeName: String = "logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            channel.exchangeDeclare(exchangeName, "fanout")
            val message =
                if (args.isEmpty()) "info: Hello World!" else args.joinToString(" ")
            channel.basicPublish(exchangeName, "", null, message.toByteArray(charset("UTF-8")))
            println(" [x] Sent '$message'")
        }
    }
}
