package com.example.rabbitmq.hellomq

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets

@SpringBootApplication
class Recv

fun main(args: Array<String>) {
    runApplication<RabbitmqApplication>(*args)
    val queueName: String = "hello"

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.queueDeclare(queueName, false, false, false, null)
    println(" [*] Waiting for messages. To exit press CTRL+C")

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '$message'")
    }
    channel.basicConsume(queueName, true, deliverCallback) { consumerTag -> }
}
