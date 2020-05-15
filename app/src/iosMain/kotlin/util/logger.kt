package util
import util.LogLevel.*
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

actual object logger {

    @SharedImmutable
    val additionalAction : AtomicReference<(String) -> Unit> = AtomicReference({ _:String-> }.freeze())

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