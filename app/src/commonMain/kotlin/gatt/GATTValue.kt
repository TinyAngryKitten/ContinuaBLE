package gatt

import util.*
import kotlin.math.pow

//Collection of datatypes from Bluetooth specification
sealed class GATTValue {
    data class SInt8(val byte : Byte) : GATTValue() {
        val value = byte.toInt()
        override fun toString(): String = value.toString()
    }
    data class SInt16(val byte1: Byte, val byte2: Byte) : GATTValue() {
        val value = byte1.toUnsignedInt() or byte2.toInt().shl(8)
        override fun toString(): String = value.toString()
    }

    data class UInt8 constructor(val byte: Byte) : GATTValue() {
        val value = byte.toUnsignedInt()
        override fun toString(): String = value.toString()
    }
    data class UInt16 constructor(val byte1 : Byte, val byte2: Byte) : GATTValue() {
        val value = byte1.toUnsignedInt() or byte2.toUnsignedInt().shl(8)
        override fun toString(): String = value.toString()
    }

    //represents a 4 bit value
    data class Nibble(val value: Byte) : GATTValue() {
        val unsigned = value.toInt().and(15)
        override fun toString(): String = "Nibble(signed: ${value.toInt()}, unsigned: $unsigned)"
    }

    sealed class Year(val value : Int) {
        class Value(value : Int) : Year(value) {
            override fun toString(): String = "Year: $value"
        }

        object Unknown : Year(0){
            override fun toString(): String {
                return "Unknown Year"
            }
        }
        object Invalid : Year(-1){
            override fun toString(): String = "Invalid Year"
        }

        companion object {
            fun fromInt(year: Int) = when(year) {
                in 1582..9999 -> Value(year)
                0 -> Unknown
                else -> Invalid
            }
        }
    }

    sealed class Month(val value : Int) {
        class Value(value : Int) : Month(value){
            override fun toString(): String {
                return "Month: $value"
            }
        }
        object Unknown : Month(0){
            override fun toString(): String {
                return "Unknown month"
            }
        }
        object Invalid : Month(-1) {
            override fun toString(): String {
                return "Invalid month"
            }
        }

        companion object {
            fun fromInt(month: Int) = when(month) {
                in 1..12 -> Value(month)
                0 -> Unknown
                else -> Invalid
            }
        }
    }

    //date and time representationf
    data class DateTime(
        val year : Year,
        val month : Month,
        val day : Int,
        val hours : Int,
        val minutes : Int,
        val seconds : Int
    ) : GATTValue() {

        constructor(
            year: UInt16,
            month: UInt8,
            day: UInt8,
            hours: UInt8,
            minutes: UInt8,
            seconds: UInt8
        ) : this(
            Year.fromInt(year.value),
            Month.fromInt(month.value),
            day.value,
            hours.value,
            minutes.value,
            seconds.value
        )

        override fun toString(): String {
            return "DateTime(\n"+
                "\t\t\t\t\tyear: ${year.value},\n"+
                "\t\t\t\t\tmonth: ${month.value},\n"+
                "\t\t\t\t\tday: $day,\n"+
                "\t\t\t\t\thour: $hours,\n"+
                "\t\t\t\t\tminute: $minutes,\n"+
                "\t\t\t\t\tsecond: $seconds\n"+
                "\t\t\t)"
        }

        fun toByteArray() = ByteArray(7) {
            when(it) {
                6 -> seconds
                5 -> minutes
                4 -> hours
                3 -> day
                2 -> month.value
                0 -> (year.value and 255)//second part of the year value
                1 -> 7//first part of the year value(valid until 2050)
                else -> seconds
            }.toByte()
        }
    }

    //32 bit floating point number
    class Float(val value : kotlin.Float) : GATTValue() {
        override fun toString(): String = value.toString()
        companion object {
            //TODO: unable to find information about the FLOAT datatype, but there are likely NaN/Nres etc. values
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
    sealed class SFloat : GATTValue() {
        override fun toString(): String = this::class.simpleName ?: ""
        object NaN : SFloat()
        object NRes : SFloat()
        object ReservedForFutureUse : SFloat()
        object PlussInfinity : SFloat()
        object MinusInfinity : SFloat()

        data class Value(val value: kotlin.Float) : SFloat() {
            override fun toString() =value.toString()
        }

        companion object {
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
    class Flags(val value : List<Boolean>) : GATTValue() {
        override fun toString(): String = value.toString()
    }

    //single boolean
    class Flag(val value : Boolean) : GATTValue() {
        override fun toString(): String = value.toString()
    }

    //UTF8 encoded string
    class UTF8(val rawBytes : ByteArray) {
        //default characterset is allways UTF-8 for String <-> Byte
        val encodedString = rawBytes.fold("") {acc, byte -> acc+byte.toChar()}
        override fun toString(): String = encodedString
    }

    object Empty : GATTValue() {
        override fun toString(): String = "Empty value"
    }
}