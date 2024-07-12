package net.tlalka.medium.async

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MergeAsyncExtKtTest {

    @Test
    fun `return pair with two results when two async jobs finished with success result`() = runTest {
        val jobA = async { Result.success(1) }
        val jobB = async { Result.success("2") }

        val result = mergeAsync(jobA, jobB)

        assertEquals(Pair(1, "2"), result.getOrThrow())
    }

    @Test
    fun `return error from first job when two async jobs finished with error`() = runTest {
        val jobA = async { Result.failure<Int>(ERROR_1) }
        val jobB = async { Result.failure<Int>(ERROR_2) }

        val result = mergeAsync(jobA, jobB)

        assertEquals(ERROR_1, result.exceptionOrNull())
    }

    @Test
    fun `return error from second job when second async job finished with error`() = runTest {
        val jobA = async { Result.success(1) }
        val jobB = async { Result.failure<Int>(ERROR_2) }

        val result = mergeAsync(jobA, jobB)

        assertEquals(ERROR_2, result.exceptionOrNull())
    }

    @Test
    fun `skip execution of second job when first job finished with error`() = runTest {
        val jobA = async<Result<Int>> { Result.failure(ERROR_1) }
        val jobB = async<Result<Int>> { delay(TEST_DELAY).let { throw IllegalStateException("BUM") } }

        val result = mergeAsync(jobA, jobB)

        assertEquals(ERROR_1, result.exceptionOrNull())
    }

    @Test
    fun `skip execution of first job when second job finished with error`() = runTest {
        val jobA = async<Result<Int>> { delay(TEST_DELAY).let { throw IllegalStateException("BUM") } }
        val jobB = async<Result<Int>> { Result.failure(ERROR_2) }

        val result = mergeAsync(jobA, jobB)

        assertEquals(ERROR_2, result.exceptionOrNull())
    }

    @Test
    fun `return triple with three results when three async jobs finished with success result`() = runTest {
        val jobA = async { Result.success(1) }
        val jobB = async { Result.success("2") }
        val jobC = async { Result.success(3L) }

        val result = mergeAsync(jobA, jobB, jobC)

        assertEquals(Triple(1, "2", 3L), result.getOrThrow())
    }

    @Test
    fun `return error from first job when three async jobs finished with error`() = runTest {
        val jobA = async { Result.failure<Int>(ERROR_1) }
        val jobB = async { Result.failure<Int>(ERROR_2) }
        val jobC = async { Result.failure<Int>(ERROR_3) }

        val result = mergeAsync(jobA, jobB, jobC)

        assertEquals(ERROR_1, result.exceptionOrNull())
    }

    @Test
    fun `return error from third job when third async job finished with error`() = runTest {
        val jobA = async { Result.success(1) }
        val jobB = async { Result.success("2") }
        val jobC = async { Result.failure<Long>(ERROR_3) }

        val result = mergeAsync(jobA, jobB, jobC)

        assertEquals(ERROR_3, result.exceptionOrNull())
    }

    @Test
    fun `skip execution of other jobs when third job finished with error`() = runTest {
        val jobA = async<Result<Int>> { Result.failure(ERROR_1) }
        val jobB = async<Result<Int>> { delay(TEST_DELAY).let { throw IllegalStateException("BUM1") } }
        val jobC = async<Result<Int>> { delay(TEST_DELAY).let { throw IllegalStateException("BUM2") } }

        val result = mergeAsync(jobA, jobB, jobC)

        assertEquals(ERROR_1, result.exceptionOrNull())
    }

    companion object {
        private const val TEST_DELAY = 1000L

        private val ERROR_1 = RuntimeException("Error 1")
        private val ERROR_2 = RuntimeException("Error 2")
        private val ERROR_3 = RuntimeException("Error 3")
    }
}