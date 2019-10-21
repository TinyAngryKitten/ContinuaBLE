package sample

actual object logger {
    actual fun printLine(str: String) {
    }

    actual val logLevel: LogLevel
        get() = TODO()

    actual fun info(str: String) {
    }

    actual fun debug(str: String) {
    }

    actual fun error(str: String) {
    }

}