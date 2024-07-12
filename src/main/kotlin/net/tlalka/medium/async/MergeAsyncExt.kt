package net.tlalka.medium.async

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend inline fun <R1 : Any, R2 : Any> mergeAsync(
    a: Deferred<Result<R1>>,
    b: Deferred<Result<R2>>
): Result<Pair<R1, R2>> {
    var aResult: Result<R1>? = null
    var bResult: Result<R2>? = null

    coroutineScope {
        launch { aResult = a.await().onFailure { b.cancel() } }
        launch { bResult = b.await().onFailure { a.cancel() } }
    }
    return runCatching {
        Pair(aResult?.getOrThrow(), bResult?.getOrThrow()).let {
            Pair(requireNotNull(it.first), requireNotNull(it.second))
        }
    }
}

suspend inline fun <R1 : Any, R2 : Any, R3 : Any> mergeAsync(
    a: Deferred<Result<R1>>,
    b: Deferred<Result<R2>>,
    c: Deferred<Result<R3>>
): Result<Triple<R1, R2, R3>> {
    var aResult: Result<R1>? = null
    var bResult: Result<R2>? = null
    var cResult: Result<R3>? = null
    val cancelJobs = { listOf(a, b, c).map { it.cancel() } }

    coroutineScope {
        launch { aResult = a.await().onFailure { cancelJobs() } }
        launch { bResult = b.await().onFailure { cancelJobs() } }
        launch { cResult = c.await().onFailure { cancelJobs() } }
    }
    return runCatching {
        Triple(aResult?.getOrThrow(), bResult?.getOrThrow(), cResult?.getOrThrow()).let {
            Triple(requireNotNull(it.first), requireNotNull(it.second), requireNotNull(it.third))
        }
    }
}