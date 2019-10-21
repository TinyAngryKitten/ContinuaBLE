package iso

import sample.logger
import util.leftMostNibble
import util.nibleToSignedInt
import util.rightMostNibble
import kotlin.math.pow

sealed class SFloat{
    object NaN : SFloat()
    object NRes : SFloat()
    object ReservedForFutureUse : SFloat()
    object PlussInfinity : SFloat()
    object MinusInfinity : SFloat()
    class Value(val floatValue: Float) : SFloat() {
        override fun toString() ="value(val: $floatValue"
    }

    //TODO: SEEMS TO NOT WORK CORRECTLY
    companion object {
        fun fromBytes(byte1 : Byte, byte2 : Byte) : SFloat {
            val exponent = nibleToSignedInt(byte2.leftMostNibble())

            val mantissa = byte1.toInt().or(byte2.rightMostNibble().shl(8))

            println(exponent)
            println(mantissa)
            println("SFLOAT: "+10f.pow(exponent) * mantissa)

            return when(val value = (10f.pow(exponent) * mantissa) ) {
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