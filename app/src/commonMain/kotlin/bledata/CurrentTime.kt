package bledata

import kotlin.time.Clock

private fun toByteArray(
    value : UInt
) = arrayOf(value.toByte()).toByteArray()

private fun toByteArray(value : Byte) = arrayOf(value).toByteArray()

data class CurrentTime(
    val exactTime: ExactTime,
    val adjustReason : AdjustReason
) {
    fun toByteArray() = toByteArray(adjustReason.toByte()) + exactTime.toByteArray()
}

data class ExactTime(
    val dayDateTime : DayDateTime,
    val fractions : UInt
) {
    fun toByteArray() = toByteArray(fractions) + dayDateTime.toByteArray()
}

data class DayDateTime(
    val dateTime : DateTime,
    val dayOfWeek : DayOfWeekEnum
) {
    fun toByteArray() = toByteArray((dayOfWeek.ordinal + 1).toUInt()) + dateTime.toByteArray()
}

data class DateTime(
    val year : UInt,
    val month : UInt,
    val day : UInt,
    val hours : UInt,
    val minutes : UInt,
    val seconds : UInt
) {
    fun toByteArray() = ByteArray(7) {
        when(it) {
            0 -> seconds
            1 -> minutes
            2 -> hours
            3 -> day
            4 -> month
            5 -> (year and Byte.MAX_VALUE.toUInt())//first part of the year value
            6 -> (year xor Byte.MAX_VALUE.toUInt())//second part of the year value
            else -> seconds//TODO: does it start at 0 or 1?
        }.toByte()
    }
}

enum class DayOfWeekEnum {
    Monday,
    Tuesday,
    Wednsday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

data class AdjustReason(
    val manualTimeUpdate : Boolean,
    val externalReferenceTimeUpdate : Boolean,
    val changeOfTimeZone : Boolean,
    val changeOfDST : Boolean
){
    fun toByte() = (0
                or if (manualTimeUpdate) 1 else 0
                or if(externalReferenceTimeUpdate) 2 else 0
                or if(changeOfTimeZone) 4 else 0
                or if(changeOfDST) 8 else 0
                ).toByte()
}