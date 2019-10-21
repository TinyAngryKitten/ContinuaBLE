package sample

expect object logger {
    val logLevel : LogLevel

    fun info(str : String)
    fun debug(str : String)
    fun error(str : String)

    fun printLine(str : String)
}


sealed class LogLevel(val level : Int) {
    object DEBUG : LogLevel(0)
    object INFO : LogLevel(1)
    object ERROR : LogLevel(2)
}