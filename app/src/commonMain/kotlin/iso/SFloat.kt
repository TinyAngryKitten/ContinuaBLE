package iso

import sample.logger
import util.leftMostNibble
import util.rightMostNibble
import kotlin.math.pow

sealed class SFloat{
    object NaN : SFloat()
    object NRes : SFloat()
    object ReservedForFutureUse : SFloat()
    object PlussInfinity : SFloat()
    object MinusInfinity : SFloat()
    class Value(val floatValue: Float) : SFloat()

    //TODO: SEEMS TO NOT WORK CORRECTLY
    companion object {
        fun fromBytes(byte1 : Byte, byte2 : Byte) : SFloat {
            val exponent = byte2.leftMostNibble()
            val mantissaHex = byte1.toString(16) + byte2.rightMostNibble().toByte().toString(16)
            val mantissa = mantissaHex.toInt(16)

            logger.debug("exponent: $exponent")
            logger.debug("mantissa: $mantissa")

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