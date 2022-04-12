package com.example.rabbitmq.hellomq

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets

@SpringBootApplication
class Send

fun main(args: Array<String>) {
    val queueName: String = "hello"

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            channel.queueDeclare(queueName, false, false, false, null)
            val message: String = "Hello World!"
            channel.basicPublish("", queueName, null, message.toByteArray(StandardCharsets.UTF_8))
            System.out.println(" [x] Sent '$message'")
        }
    }
}
