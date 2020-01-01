package summer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import summer.execution.*
import summer.execution.source.SourceExecutor
import summer.execution.source.SummerSource
import summer.execution.source.execute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SourceExecutorTests {

    @Test
    fun success() {

        val expectedResult = "test"

        val scope = SummerExecutor(Dispatchers.Unconfined, Dispatchers.Unconfined, loggersFactory)
        val deferredExecutor = DeferredExecutor(scope)

        val source = object : SummerSource<String, Unit> {

            override suspend fun invoke(params: Unit): String {
                return expectedResult
            }
        }

        lateinit var actualResult: String
        val executor = SourceExecutor(
            source = source,
            deferredExecutor = deferredExecutor,
            interceptor = NoInterceptor(),
            onExecute = {},
            onFailure = { _, _ -> },
            onCancel = {},
            onSuccess = { result, _ ->
                actualResult = result
            },
            scope = scope,
            workContext = Dispatchers.Unconfined
        )

        executor.execute()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun fail() {

        val scope = SummerExecutor(Dispatchers.Unconfined, Dispatchers.Unconfined, loggersFactory)
        val deferredExecutor = DeferredExecutor(scope)

        val source = object : SummerSource<String, Unit> {

            override suspend fun invoke(params: Unit): String {
                throw RuntimeException()
            }
        }

        lateinit var throwable: Throwable
        val executor = SourceExecutor(
            source = source,
            deferredExecutor = deferredExecutor,
            interceptor = NoInterceptor(),
            onExecute = {},
            onFailure = { e, _ ->
                throwable = e
            },
            onCancel = {},
            onSuccess = { _, _ -> },
            scope = scope,
            workContext = Dispatchers.Unconfined
        )

        executor.execute()

        assertNotNull(throwable)
    }

    @Test
    fun cancel() {

        val scope = SummerExecutor(Dispatchers.Unconfined, Dispatchers.Unconfined, loggersFactory)
        val deferredExecutor = DeferredExecutor(scope)

        val source = object : SummerSource<String, Unit> {

            override suspend fun invoke(params: Unit): String {
                delay(100)
                return "test"
            }
        }

        var isCancelled = false
        var throwable: Throwable? = null
        val executor = SourceExecutor(
            source = source,
            deferredExecutor = deferredExecutor,
            interceptor = NoInterceptor(),
            onExecute = {},
            onFailure = { e, _ ->
                throwable = e
            },
            onCancel = {
                isCancelled = true
            },
            onSuccess = { _, _ -> },
            scope = scope,
            workContext = Dispatchers.Unconfined
        )

        executor.execute()
        scope.cancel()

        assertNull(throwable)
        assertTrue(isCancelled)
    }

    @Test
    fun cancelBeforeLaunch() {

        val scope = SummerExecutor(Dispatchers.Unconfined, Dispatchers.Unconfined, loggersFactory)
        val deferredExecutor = DeferredExecutor(scope)

        val source = object : SummerSource<String, Unit> {

            override suspend fun invoke(params: Unit): String {
                return "test"
            }
        }

        var isOnSuccessCalled = false
        var isOnCancelCalled = false
        var throwable: Throwable? = null
        val executor = SourceExecutor(
            source = source,
            deferredExecutor = deferredExecutor,
            interceptor = NoInterceptor(),
            onExecute = {},
            onFailure = { e, _ ->
                throwable = e
            },
            onCancel = {
                isOnCancelCalled = true
            },
            onSuccess = { _, _ ->
                isOnSuccessCalled = true
            },
            scope = scope,
            workContext = Dispatchers.Unconfined
        )

        scope.cancel()
        executor.execute()

        assertNull(throwable)
        assertFalse(isOnSuccessCalled)
        assertFalse(isOnCancelCalled)
    }
}