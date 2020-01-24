package iso

import util.*
import kotlin.experimental.or
import kotlin.math.pow


sealed class ISOValue {
    data class SInt8(val value : Int) : ISOValue()
    data class SInt16(val value : Int) : ISOValue()

    data class UInt8 @ExperimentalUnsignedTypes constructor(val value : UInt) : ISOValue()
    data class UInt16 @ExperimentalUnsignedTypes constructor(val value : UInt) : ISOValue()

    data class DateTime(
        val year : UInt,
        val month : UInt,
        val day : UInt,
        val hours : UInt,
        val minutes : UInt,
        val seconds : UInt
    ) : ISOValue() {

        constructor(
            year: UInt16,
            month: UInt8,
            day: UInt8,
            hours: UInt8,
            minutes: UInt8,
            seconds: UInt8
        ) : this(
            year.value,
            month.value,
            day.value,
            hours.value,
            minutes.value,
            seconds.value
        )

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

    sealed class SFloat : ISOValue() {
        object NaN : SFloat()
        object NRes : SFloat()
        object ReservedForFutureUse : SFloat()
        object PlussInfinity : SFloat()
        object MinusInfinity : SFloat()

        data class Value(val value: Float) : SFloat() {
            override fun toString() ="value(val: $value"
        }

        companion object {
            fun fromBytes(byteArray : ByteArray) = fromBytes(byteArray[0],byteArray[1])

            fun fromBytes(byte1 : Byte, byte2 : Byte) : SFloat {
                val exponent = nibleToSignedInt(byte2.leftMostNibble())

                val mantissa = byte1.or(byte2.rightMostNibble().shl(8).toByte())

                return when(val value = (10f.pow(exponent) * mantissa.toInt()) ) {
                    0x07FF.toFloat() -> NaN
                    0x0800.toFloat() -> NRes
                    0x07FE.toFloat() -> PlussInfinity
                    0x0802.toFloat() -> MinusInfinity
                    0x0801.toFloat() -> ReservedForFutureUse
                    else -> Value(value)
                }
            }
        }
    }

    class Flags(val value : List<Boolean>) : ISOValue()

    class Flag(val value : Boolean) : ISOValue()

    class UTF8(val rawBytes : ByteArray) {
        //default characterset is allways UTF-8 for String <-> Byte
        val encodedString = rawBytes.fold("") {acc, byte -> acc+byte.toChar()}
    }

    object Empty : ISOValue()
}