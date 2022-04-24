package com.example.rabbitmq.pubsub.direct

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmitLogDirect

private fun getSeverity(args: Array<String>): String {
    return args[0]
}

private fun getMessage(args: Array<String>): String {
    return args[1]
}

fun main(args: Array<String>) {

    val exchangeName: String = "direct_logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->

            /** @desc exchange type - direct
             * a message goes to the queues whose binding key exactly matches the routing key of the message.
             * */
            channel.exchangeDeclare(exchangeName, "direct")

            val severity: String = getSeverity(args)
            val message: String = getMessage(args)

            channel.basicPublish(exchangeName, severity, null, message.toByteArray(charset("UTF-8")))
            println(" [x] Sent '$severity':'$message'")
        }
    }
}
