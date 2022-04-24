package com.example.rabbitmq.pubsub.topic

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/** @DESC
* args
 * - "kern.critical" "A critical kernel error"
 * - "#" "A critical kernel error"
* */
@SpringBootApplication
class EmitLogTopic

private fun getTopic(args: Array<String>): String {
    return args[0]
}

private fun getMessage(args: Array<String>): String {
    return args[1]
}

fun main(args: Array<String>) {

    val exchangeName: String = "topic_logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->

            channel.exchangeDeclare(exchangeName, "topic")

            val routingKey: String = getTopic(args)
            val message: String = getMessage(args)

            channel.basicPublish(exchangeName, routingKey, null, message.toByteArray(charset("UTF-8")))
            println(" [x] Sent '$routingKey':'$message'");
        }
    }
}
