package com.example

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Result
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import java.util.*

fun main(args: Array<String>) {
    println("START THE TESTING >>")

    val connectionFactory = ConnectionFactories.get("r2dbc:sqlserver://sa:sa123@localhost:1433/selfinquiry")
    val connection = Mono.from(connectionFactory.create())

    connection.flatMap {
        val result = it.createStatement("insert into customer (customer_id, name) values (@id, @name)")
            .bind("id", UUID.randomUUID().toString())
            .bind("name", "Name Longer than 10 Characters")
            .execute()

        Mono.from(result)
    }.flatMap {
        println("Row Updated : ${it.rowsUpdated}")

        Mono.from(it.rowsUpdated)
    }.doOnError {
        println("Got error >>> ${it.message}")

        it.printStackTrace()
    }.block()
}