package bledata

import kotlin.time.Clock

data class CurrentTime(
    val exactTime: ExactTime,
    val adjustReason : AdjustReason
) {
    fun toByteArray() = exactTime.toByteArray() + adjustReason.toByte()
}

data class ExactTime(
    val dayDateTime : DayDateTime,
    val fractions : UInt
) {
    fun toByteArray() = dayDateTime.toByteArray() + fractions.toByte()
}

data class DayDateTime(
    val dateTime : DateTime,
    val dayOfWeek : DayOfWeekEnum
) {
    fun toByteArray() = dateTime.toByteArray() + (dayOfWeek.ordinal + 1).toByte()
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
            6 -> seconds
            5 -> minutes
            4 -> hours
            3 -> day
            2 -> month
            1 -> (year xor Byte.MAX_VALUE.toUInt())//second part of the year value
            0 -> (year and Byte.MAX_VALUE.toUInt())//first part of the year value
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