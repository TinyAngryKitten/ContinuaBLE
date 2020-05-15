package util
import util.LogLevel.*
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

actual object logger {

    @SharedImmutable
    private val addToView : AtomicReference<(String) -> Unit> = AtomicReference({ _:String-> }.freeze())
    fun setAddToView(fn: (String)-> Unit) = addToView.compareAndSet(addToView.value, fn)

    actual fun printLine(str: String) {
        println(str)
        //additionalAction.value(str)
    }

    actual fun info(str: String) {
        addToView.value(str+"\n")
        printLine(str)
    }

    actual fun debug(str: String) = printLine(str)

    actual fun error(str: String) =printLine(str)
}