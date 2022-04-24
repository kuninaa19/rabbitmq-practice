package com.example.rabbitmq.pubsub.direct

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets


@SpringBootApplication
class ReceiveLogsDirect

fun main(args: Array<String>) {

    val exchangeName: String = "direct_logs";

    runApplication<RabbitmqApplication>(*args)

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.exchangeDeclare(exchangeName, "direct")
    val queueName: String = channel.queueDeclare().queue

    if (args.isEmpty()) {
        System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
        System.exit(1);
    }

    /** @Desc 특정한 값에 해당하는 데이터만 받도록 routingKey 설정 */
    for (severity in args) {
        channel.queueBind(queueName, exchangeName, severity)
    }
    println(" [*] Waiting for messages. To exit press CTRL+C");

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '" + delivery.envelope.routingKey + "':'" + message + "'");
    }
    channel.basicConsume(queueName, true, deliverCallback) { consumerTag -> }

}
