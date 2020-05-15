package sample

import platform.darwin.dispatch_queue_t
import util.logger
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

object GlobalSingleton {
    @SharedImmutable
    val additionalAction : AtomicReference<(String) -> Unit> = AtomicReference({ _:String-> logger.debug("ADDITIONAL ACTION") }.freeze())

    @SharedImmutable
    val nsdata : AtomicReference<Any?> = AtomicReference(null)

    @SharedImmutable
    val dispatchQueue : AtomicReference<dispatch_queue_t> = AtomicReference(null)

        fun initializeGlobals(printToScreen : (String) -> Unit, nsdData: Any?, queue : dispatch_queue_t) {
        additionalAction.compareAndSet(additionalAction.value,printToScreen)
        nsdata.compareAndSet(nsdata.value,nsdData)
        dispatchQueue.compareAndSet(dispatchQueue.value,queue)
    }

}