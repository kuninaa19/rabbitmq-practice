package com.example.rabbitmq.workqueues

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets

/** @desc Producer */
/**  @param args : First message. Second message.. Third message... */
@SpringBootApplication
class NewTask

fun main(args: Array<String>) {
    runApplication<RabbitmqApplication>(*args)
    val queueName: String = "task_queue"

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            channel.queueDeclare(queueName, true, false, false, null)

            val message: String = args.joinToString(" ")

            channel.basicPublish("", queueName, null, message.toByteArray(StandardCharsets.UTF_8))
            println(" [x] Sent '$message'")
        }
    }
}