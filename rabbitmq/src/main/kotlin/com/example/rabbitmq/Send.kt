package com.example.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
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
