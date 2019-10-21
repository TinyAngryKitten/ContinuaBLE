package iso

import kotlin.math.ceil

//this is bad, probably dont use it...
sealed class MessagePosition

data class FlagPosition(
    val bytePosition : Int,
    val bitPosition : Int
) : MessagePosition()

data class ValuePosition(
    val bytePosition : Int,
    val bitPosition : Int,
    val length : Int
) : MessagePosition() {
    fun getRangeOfBytes() : IntRange =
        IntRange(
            bytePosition,
            bytePosition + ceil( length.div(8f)).toInt()
        )
}