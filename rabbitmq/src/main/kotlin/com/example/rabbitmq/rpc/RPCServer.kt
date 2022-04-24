package com.example.rabbitmq.rpc

import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@SpringBootApplication
class RPCServer

private val rpcQueueName = "rpc_queue"

private fun fib(n: Int): Int {
    if (n == 0) return 0
    return if (n == 1) 1 else fib(n - 1) + fib(n - 2)
}

fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    factory.username = "user"
    factory.password = "password"
    factory.host = "localhost"

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            channel.queueDeclare(rpcQueueName, false, false, false, null)
            channel.queuePurge(rpcQueueName)
            channel.basicQos(1)

            println(" [x] Awaiting RPC requests")

            val lock = ReentrantLock()
            val condition = lock.newCondition()

            val deliverCallback =
                DeliverCallback { consumerTag: String?, delivery: Delivery ->
                    val replyProps = AMQP.BasicProperties.Builder()
                        .correlationId(delivery.properties.correlationId)
                        .build()

                    var response = ""
                    try {
                        val message = String(delivery.body, StandardCharsets.UTF_8)
                        val n = message.toInt()
                        println(" [.] fib($message)")
                        response += fib(n)
                    } catch (e: RuntimeException) {
                        println(" [.] $e")
                    } finally {
                        channel.basicPublish(
                            "",
                            delivery.properties.replyTo,
                            replyProps,
                            response.toByteArray(charset("UTF-8"))
                        )
                        channel.basicAck(delivery.envelope.deliveryTag, false)
                        // RabbitMq consumer worker thread notifies the RPC server owner thread

                        lock.withLock { condition.signal() }
                    }
                }
            channel.basicConsume(
                rpcQueueName,
                false,
                deliverCallback
            ) { consumerTag: String? -> }
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                lock.withLock {
                    try {
                        condition.await()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}