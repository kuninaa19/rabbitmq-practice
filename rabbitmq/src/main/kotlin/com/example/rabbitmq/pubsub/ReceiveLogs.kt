package com.example.rabbitmq.pubsub

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets


@SpringBootApplication
class ReceiveLogs

fun main(args: Array<String>) {

    val exchangeName: String = "logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.exchangeDeclare(exchangeName, "fanout")
    val queueName: String = channel.queueDeclare().queue
    channel.queueBind(queueName, exchangeName, "")

    println(" [*] Waiting for messages. To exit press CTRL+C")

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '$message'")
    }
    channel.basicConsume(queueName, true, deliverCallback) { consumerTag -> }

}
