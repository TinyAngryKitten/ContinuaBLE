package iso

import data.DataRecord
import data.EmptyRecord
import sample.logger
import util.*
import kotlin.math.floor
import kotlin.math.pow

//
fun parse(bytes: ByteArray, fn : ISOParser.()->DataRecord) : DataRecord = try {
    ISOParser(bytes).run(fn)
} catch (e : Exception) {
    logger.error("an error occured while parsing a BLEReading: " + (e.message ?: e.toString()) )
    EmptyRecord
}

/**
 * Defines a DSL for parsing BLEs data
 * for every value read the corresponding
 * bytes are removed to keep track of whats next
 * When parsing is completes, bytes will be
 */
class ISOParser(var bytes : ByteArray) {
    private var flagBytes : ByteArray = byteArrayOf()

    fun <T : ISOValue?> dropThen(bytesToDrop : Int, fn : () -> T?) : T? {
        take(bytesToDrop)
        return fn()
    }

    /**
     * if condition is true, parse part of the byte array, if not then return an empty value
     * this is used to read optional values that can exist or not
     */
    fun <T : ISOValue?> onCondition(condition : Boolean, fn : () -> T?) : T? =
        if(condition) fn()
        else null

    /**
     * turn 2 bytes into a floating point number
     * the the leftmost nibble represent an exponent of 10
     * and number representation of remaining bytes are multiplied with 10.pow(exponent)
     */
    val sfloat = {
        ISOValue.SFloat.fromBytes(take(2))
    }

    /**
     * turn 2 bytes into a unsigned integer
     */
    val uint16 = {
        ISOValue.UInt16(
            parseUInt16(take(2))
        )
    }


    /**
     * turn a byte into an unsigned integer
     */
    val uint8 = {
            ISOValue.UInt8(take(1).first().toUInt())
    }

    val utf8 = {
        nrOfBytes : Int ->
        ISOValue.UTF8(take(nrOfBytes))
    }

    /**
     * turn a byte into a list of booleans
     */
    val flags = {
        val byte = take(1).first()

        ISOValue.Flags(
            (0..7).map { byte.positiveBitAt(it) }
        )
    }

    /**
     * used to add a boolean as iso value
     */
    val boolean : (Boolean) -> ISOValue.Flag = {
            bool : Boolean ->
        ISOValue.Flag(bool)
    }

    /**
     * takes removes a number of bytes from the byte array and returns them
     */
    fun take(nrOfBytes :Int) : ByteArray{
        val returnValue = bytes.sliceArray(0 until nrOfBytes)
        bytes = bytes.sliceArray(nrOfBytes until bytes.size)
        return returnValue
    }

    /**
     * returns true if the nth bit in the flag bytes are 1 and false if not
     * will throw index out of bounds
     */
    fun flag(index : Int) : Boolean =
        if(index == 0) flagBytes[0].positiveBitAt(0)
        else flagBytes[
            floor(index.toFloat().div(8f)).toInt()
        ].positiveBitAt(index % 8)

    fun flags(range : IntRange) {
        flagBytes = bytes.slice(range).toByteArray()
        bytes = bytes.sliceArray(0 until range.first) + bytes.sliceArray(range.last until bytes.size)
    }
}