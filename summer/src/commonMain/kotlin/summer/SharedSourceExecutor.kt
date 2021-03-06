package summer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import summer.log.KLogging
import kotlin.coroutines.CoroutineContext

class SharedSourceExecutor<TEntity, in TParams>(
    private val source: SummerSharedSource<TEntity, TParams>,
    private val action: suspend (Deferred<TEntity>, TParams?) -> Unit,
    private val scope: CoroutineScope,
    private val workContext: CoroutineContext
) : SummerSharedSource.Observer<TEntity, TParams> {

    override fun onObtain(deferred: Deferred<TEntity>, params: TParams?) {
        scope.launch {
            val scopedDeferred = async(workContext) {
                deferred.await()
            }
            action(scopedDeferred, params)
        }
    }

    fun execute(params: TParams) {
        source(params)
    }

    companion object : KLogging()
}

fun <TEntity> SharedSourceExecutor<TEntity, Unit>.execute() = execute(Unit)