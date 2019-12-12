package iso

import util.leftMostNibble
import util.nibleToSignedInt
import util.rightMostNibble
import kotlin.math.pow


sealed class ISOValue {
    data class SInt8(val value : Int) : ISOValue()
    data class SInt16(val value : Int) : ISOValue()

    data class UInt8 @ExperimentalUnsignedTypes constructor(val value : UInt) : ISOValue()
    data class UInt16 @ExperimentalUnsignedTypes constructor(val value : UInt) : ISOValue()

    sealed class SFloat() : ISOValue() {
        object NaN : SFloat()
        object NRes : SFloat()
        object ReservedForFutureUse : SFloat()
        object PlussInfinity : SFloat()
        object MinusInfinity : SFloat()
        data class Value(val value: kotlin.Float) : SFloat() {
            override fun toString() ="value(val: $value"
        }

        companion object {
            fun fromBytes(byteArray : ByteArray) = fromBytes(byteArray[0],byteArray[1])

            fun fromBytes(byte1 : Byte, byte2 : Byte) : SFloat {
                val exponent = nibleToSignedInt(byte2.leftMostNibble())

                //lazy solution, turn it to UInt so it wont have a negative value if last bit is 1
                val mantissa = byte1.toUInt().or(byte2.rightMostNibble().shl(8).toUInt())

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

    class Float(val value : kotlin.Float) : ISOValue()

    class Flags(val value : List<Boolean>) : ISOValue()

    class Flag(val value : Boolean) : ISOValue()

    class UTF8(val rawBytes : ByteArray) {
        //default characterset is allways UTF-8 for String <-> Byte
        val encodedString = String(
            CharArray(rawBytes.size) { it.toChar() }
        )
    }

    object Empty : ISOValue() {
        val value = null
    }
}