package bledata

import gatt.GATTValue

data class CurrentTime(
    val exactTime: ExactTime,
    val adjustReason : AdjustReason
) {
    fun toByteArray() = exactTime.toByteArray() + adjustReason.toByte()
}

data class ExactTime(
    val dayDateTime : DayDateTime,
    val fractions : Int
) {
    constructor(
        dayDateTime: DayDateTime,
        fractions: GATTValue.UInt8
    ) : this(dayDateTime,fractions.value)

    fun toByteArray() = dayDateTime.toByteArray() + fractions.toByte()
}

data class DayDateTime(
    val dateTime : GATTValue.DateTime,
    val dayOfWeek : DayOfWeekEnum
) {
    fun toByteArray() = dateTime.toByteArray() + (dayOfWeek.ordinal + 1).toByte()
}

enum class DayOfWeekEnum {
    Monday,
    Tuesday,
    Wednsday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
    ValueOutOfRange;

    companion object {
        fun fromValue(v: Int) = when(v) {
            0->Monday
            1->Tuesday
            2->Wednsday
            3->Thursday
            4->Friday
            5->Saturday
            6->Sunday
            else->ValueOutOfRange
        }
        fun fromUInt8(v : GATTValue.UInt8) = fromValue(v.value.toInt())
    }
}

data class AdjustReason(
    val manualTimeUpdate : Boolean = false,
    val externalReferenceTimeUpdate : Boolean = false,
    val changeOfTimeZone : Boolean = false,
    val changeOfDST : Boolean = false
){
    constructor(
        manualTimeUpdate: GATTValue.Flag,
        externalReferenceTimeUpdate: GATTValue.Flag,
        changeOfTimeZone: GATTValue.Flag,
        changeOfDST: GATTValue.Flag
    ) : this(
        manualTimeUpdate.value,
        externalReferenceTimeUpdate.value,
        changeOfTimeZone.value,
        changeOfDST.value
    )

    fun toByte() = (0
                or if (manualTimeUpdate) 1 else 0
                or if(externalReferenceTimeUpdate) 2 else 0
                or if(changeOfTimeZone) 4 else 0
                or if(changeOfDST) 8 else 0
                ).toByte()
}