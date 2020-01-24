package sample

import sample.LogLevel.*
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

actual object logger {
    @SharedImmutable
    val additionalAction : AtomicReference<(String) -> Unit> = AtomicReference({_:String->/*String->println("ATOMIC REF NOT CHANGED")*/}.freeze())

    actual fun printLine(str: String) {
        //println(str)
        additionalAction.value(str)
    }

    actual fun info(str: String) = if(logLevel.level >= INFO.level) printLine(str) else Unit

    actual fun debug(str: String) = if(logLevel.level >= DEBUG.level) printLine(str) else Unit

    actual fun error(str: String) = if(logLevel.level >= ERROR.level) printLine(str) else Unit

    actual val logLevel: LogLevel
        get() = DEBUG
}