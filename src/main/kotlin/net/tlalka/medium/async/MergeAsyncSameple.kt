package net.tlalka.medium.async

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

suspend fun funcA(): Result<String> = runCatching {
    delay(timeMillis = 400)
    return@runCatching "Hello"
}

suspend fun funcB(): Result<Int> = runCatching {
    delay(timeMillis = 100)
    throw RuntimeException("Error")
}

suspend fun naiveWay() {
    val message = funcA()
    val value = funcB()

    if (message.isSuccess && value.isSuccess) {
        println("naiveWay: $message $value")
    }
}

suspend fun asyncWay() = coroutineScope {
    val message = async { funcA() }
    val value = async { funcB() }

    val messageResult = message.await()
    val valueResult = value.await()

    if (messageResult.isSuccess && valueResult.isSuccess) {
        println("asyncWay: $messageResult $valueResult")
    }
}

suspend fun asyncAllWay() = coroutineScope {
    val message = async { funcA() }
    val value = async { funcB() }

    val results = awaitAll(message, value)

    if (results[0].isSuccess && results[1].isSuccess) {
        println("asyncAllWay: ${results[0]} ${results[1]}")
    }
}

suspend fun asyncAllWithCancel() = coroutineScope {
    launch {
        val message = async { funcA().onFailure { cancel() } }
        val value = async { funcB().onFailure { cancel() } }

        val result = awaitAll(message, value)

        if (result[0].isSuccess && result[1].isSuccess) {
            println("asyncAllWithCancel: ${result[0]} ${result[1]}")
        }
    }
}

suspend fun fixedMergeAsyncWay() = coroutineScope {
    val message = async { funcA() }
    val value = async { funcB() }

    mergeAsync(message, value)
        .onSuccess { println("Success: ${it.first} ${it.second}") }
}

fun main() {
    runBlocking {
        measureTimeMillis { naiveWay() }
            .also { println("naiveWay: $it ms") }

        measureTimeMillis { asyncWay() }
            .also { println("asyncWay: $it ms") }

        measureTimeMillis { asyncAllWay() }
            .also { println("asyncAllWay: $it ms") }

        measureTimeMillis { asyncAllWithCancel() }
            .also { println("asyncAllWithCancel: $it ms") }

        measureTimeMillis { fixedMergeAsyncWay() }
            .also { println("fixedMergeAsyncWay: $it ms") }
    }
}
