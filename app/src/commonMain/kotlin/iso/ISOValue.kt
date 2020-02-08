package iso

import util.*
import kotlin.experimental.or
import kotlin.math.pow


sealed class ISOValue {
    data class SInt8(val byte : Byte) : ISOValue() {
        val value = byte.toInt()
    }
    data class SInt16(val byte1: Byte, val byte2: Byte) : ISOValue() {
        val value = byte1.toUnsignedInt() or byte2.toInt().shl(8)
    }

    data class UInt8 constructor(val byte: Byte) : ISOValue() {
        val value = byte.toUnsignedInt()
    }
    data class UInt16 constructor(val byte1 : Byte, val byte2: Byte) : ISOValue() {
        val value = byte1.toUnsignedInt() or byte2.toUnsignedInt().shl(8)
    }

    //represents a 4 bit value
    data class Nibble(val value: Byte) : ISOValue() {
        val unsigned = value.toInt().and(15)
    }

    //date and time representationf
    data class DateTime(
        val year : Int,
        val month : Int,
        val day : Int,
        val hours : Int,
        val minutes : Int,
        val seconds : Int
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

        override fun toString(): String {
            return """
                year: $year,
                month: $month,
                day: $day,
                hour: $hours,
                minute: $minutes,
                second: $seconds,
            """.trimIndent()
        }

        fun toByteArray() = ByteArray(7) {
            when(it) {
                6 -> seconds
                5 -> minutes
                4 -> hours
                3 -> day
                2 -> month
                1 -> (year xor Byte.MAX_VALUE.toInt())//second part of the year value
                0 -> (year and Byte.MAX_VALUE.toInt())//first part of the year value
                else -> seconds//TODO: does it start at 0 or 1?
            }.toByte()
        }
    }

    //32 bit floating point number
    class Float(val value : kotlin.Float) : ISOValue() {
        companion object {
            //unable to find information about the FLOAT datatype, but there are likely NaN/Nres etc. values
            fun from(rightMostManitssa : SInt16, leftMostMantissa: SInt8, exponent: SInt8) : Float {
                var mantissa = leftMostMantissa.value.shl(16)
                for (i in 0..15) {
                    mantissa = mantissa or (rightMostManitssa.value and (1 shl i))
                }
                return Float(mantissa * 10f.pow(exponent.value))
            }
        }
    }

    //short float 16 bit
    sealed class SFloat : ISOValue() {
        object NaN : SFloat()
        object NRes : SFloat()
        object ReservedForFutureUse : SFloat()
        object PlussInfinity : SFloat()
        object MinusInfinity : SFloat()

        data class Value(val value: kotlin.Float) : SFloat() {
            override fun toString() ="value(val: $value)"
        }

        companion object {
            //fun fromBytes(byteArray : ByteArray) = fromBytes(byteArray[0],byteArray[1])

            fun fromBytes(mantissaByte: UInt8, mantissaNibble: Nibble, exponent: Nibble) : SFloat {

                var mantissa = mantissaNibble.value.toInt().shl(8)
                for(i in 0..7) {
                    mantissa = mantissa or (mantissaByte.value.toInt() and 1.shl(i))
                }

                return when(val value = (10f.pow(exponent.value.toInt()) * mantissa) ) {
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

    //list of booleans
    class Flags(val value : List<Boolean>) : ISOValue()

    //single boolean
    class Flag(val value : Boolean) : ISOValue()

    //UTF8 encoded string
    class UTF8(val rawBytes : ByteArray) {
        //default characterset is allways UTF-8 for String <-> Byte
        val encodedString = rawBytes.fold("") {acc, byte -> acc+byte.toChar()}
    }

    object Empty : ISOValue()
}