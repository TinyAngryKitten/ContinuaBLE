package util

import android.util.Log
import util.LogLevel

actual object logger {
    var additionalAction : (String) -> Unit = {_->}
    actual fun printLine(str: String) {
        Log.println(0,"",str)
    }

    actual val logLevel: LogLevel
        get() = TODO()

    actual fun info(str: String) {
        Log.i("INFO",str)
        additionalAction(str+"\n")
    }

    actual fun debug(str: String) {
        Log.d("DEBUGG",str)
    }

    actual fun error(str: String) {
        Log.e("",str)
    }

}