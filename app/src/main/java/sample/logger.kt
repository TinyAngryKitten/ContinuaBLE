package sample

import android.util.Log

actual object logger {
    actual fun printLine(str: String) {
        Log.println(0,"",str)
    }

    actual val logLevel: LogLevel
        get() = TODO()

    actual fun info(str: String) {
        Log.i("",str)
    }

    actual fun debug(str: String) {
        Log.d("DEBUGG",str)
    }

    actual fun error(str: String) {
        Log.e("",str)
    }

}