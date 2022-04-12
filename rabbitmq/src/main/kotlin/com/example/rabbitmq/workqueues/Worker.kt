package com.example.rabbitmq.workqueues

import com.example.rabbitmq.RabbitmqApplication
import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets

/** @desc Consumer */
@SpringBootApplication
class Worker

fun main(args: Array<String>) {
    runApplication<RabbitmqApplication>(*args)
    val queueName: String = "task_queue"

    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.queueDeclare(queueName, true, false, false, null)
    println(" [*] Waiting for messages. To exit press CTRL+C")

    channel.basicQos(1);

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '$message'")
        try {
            doWork(message)
        } finally {
            println("[x] Done")
            channel.basicAck(delivery.envelope.deliveryTag, false);
        }
    }
    channel.basicConsume(queueName, false, deliverCallback) { consumerTag: String? -> }
}

private fun doWork(task: String) {
    for (ch in task.toCharArray()) {
        if (ch == '.') Thread.sleep(1000)
    }
}