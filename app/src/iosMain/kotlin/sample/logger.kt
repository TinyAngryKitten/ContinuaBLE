package sample

import platform.darwin.dispatch_queue_t
import sample.GlobalSingleton.additionalAction
import sample.LogLevel.*
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

actual object logger {
    actual fun printLine(str: String) {
        println(str)
        //additionalAction.value(str)
    }

    actual fun info(str: String) {
        additionalAction.value(str+"\n")
        printLine(str)
    }

    actual fun debug(str: String) = printLine(str)

    actual fun error(str: String) =printLine(str)

    actual val logLevel: LogLevel
        get() = INFO
}