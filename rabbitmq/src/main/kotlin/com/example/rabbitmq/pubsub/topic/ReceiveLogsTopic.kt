package com.example.rabbitmq.pubsub.topic

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets

/** @DESC
 * args
 * - "*.critical" "A critical kernel error"
 * - "#" "A critical kernel error"
 * */

@SpringBootApplication
class ReceiveLogsTopic

fun main(args: Array<String>) {

    val exchangeName: String = "topic_logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.exchangeDeclare(exchangeName, "topic")
    val queueName: String = channel.queueDeclare().queue

    if (args.isEmpty()) {
        System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
        System.exit(1);
    }

    for (bindingKey in args) {
        channel.queueBind(queueName, exchangeName, bindingKey)
    }
    println(" [*] Waiting for messages. To exit press CTRL+C");

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '" + delivery.envelope.routingKey + "':'" + message + "'");
    }
    channel.basicConsume(queueName, true, deliverCallback) { consumerTag -> }

}
