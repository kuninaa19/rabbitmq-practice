package com.example.rabbitmq.rpc

import com.rabbitmq.client.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeoutException

@SpringBootApplication
class RPCClient : AutoCloseable {
    private val connection: Connection
    private val channel: Channel
    private val requestQueueName = "rpc_queue"

    @Throws(IOException::class, InterruptedException::class)
    fun call(message: String): String {
        val corrId = UUID.randomUUID().toString()
        val replyQueueName = channel.queueDeclare().queue
        val props = AMQP.BasicProperties.Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .build()

        channel.basicPublish("", requestQueueName, props, message.toByteArray(charset("UTF-8")))

        val response: BlockingQueue<String> = ArrayBlockingQueue(1)

        val ctag = channel.basicConsume(replyQueueName, true, { consumerTag, delivery ->
            if (delivery.properties.correlationId.equals(corrId)) {
                response.offer(String(delivery.body, StandardCharsets.UTF_8))
            }
        }) { consumerTag -> }
        val result = response.take()
        channel.basicCancel(ctag)
        return result
    }

    @Throws(IOException::class)
    override fun close() {
        connection.close()
    }

    companion object {
        @JvmStatic
        fun main(argv: Array<String>) {
            try {
                RPCClient().use { fibonacciRpc ->
                    for (i in 0..31) {
                        val i_str = Integer.toString(i)
                        println(" [x] Requesting fib($i_str)")
                        val response = fibonacciRpc.call(i_str)
                        println(" [.] Got '$response'")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    init {
        val factory = ConnectionFactory()
        factory.host = "localhost"
        factory.password = "password"
        factory.username = "user"
        connection = factory.newConnection()
        channel = connection.createChannel()
    }
}