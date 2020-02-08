package iso

import bledata.BLEReading
import data.DataRecord
import data.EmptyRecord
import util.*
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.floor

//
fun parse(reading: BLEReading, fn : ISOParser.()->DataRecord) : DataRecord = try {
    ISOParser(reading.data).run(fn)
} catch (e : Exception) {
    //logger.error("an error occured while parsing a BLEReading (device: ${reading.device}, characteristic: ${reading.characteristic}: " + (e.message ?: e.toString()) )
    println(e)
    println("PARSE FAILED")
    EmptyRecord(reading.device)
}

/**
 * Defines a DSL for parsing BLEs data
 * for every value read the corresponding
 * bytes are removed to keep track of whats next,
 * will throw exceptions if it encounters any null
 * values or index is read out of bounds.
 */
class ISOParser(var bytes : ByteArray) {
    private var flagBytes : ByteArray = byteArrayOf()

    //when left or right nibble is called, take 1 byte and store it in nibble byte,
    //register which side has been viewed, if both sides has been viewed or one side is viewed twice, drop nibble byte
    private var nibbleByte : Byte? = null
    private var leftNibbleViewed = false
    private var rightNibbleViewed = false

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
        ISOValue.SFloat.fromBytes(
            uint8(),
            rightNibble(),
            leftNibble()
        )
    }

    /**
     * Turn 4 bytes into a floating point number,
     * 3 right most bytes represents the mantissa
     * the last byte represents the exponent of 10
     */
    val float = {
        ISOValue.Float.from(
            sint16(),
            sint8(),
            sint8()
        )
    }

    /**
     * reads the leftmost 4 bits of a byte,
     * will take a byte from bytes if nibble byte is null or the left nibble has been read before
     */
    val leftNibble = {
        if(nibbleByte == null || leftNibbleViewed) {
            newNibbleByte()
            nibbleByte = take(1)[0]
        }

        leftNibbleViewed = true

        var nibbleValue = 240.toByte().and(nibbleByte!!)
            .toUnsignedInt().shr(4).toByte()

        //fill with 1s if the number is in 2s compliments
        if(nibbleValue.positiveBitAt(3)) nibbleValue = nibbleValue.or(240.toByte())

        ISOValue.Nibble(nibbleValue)
    }

    /**
     * reads the rightmost 4 bits of a byte,
     * will take a byte from bytes if nibble byte is null or the right nibble has been read before
     */
    val rightNibble = {
        if(nibbleByte == null || rightNibbleViewed) {
            newNibbleByte()
            nibbleByte = take(1)[0]
        }

        rightNibbleViewed = true

        var nibbleValue = 15.toByte().and(nibbleByte!!)
        if(nibbleValue.positiveBitAt(3)) nibbleValue = 240.toByte().or(nibbleValue)

        ISOValue.Nibble(nibbleValue)
    }

    /**
     * remove current nibble byte and mark both nibbles as not read
     */
    fun newNibbleByte() {
        nibbleByte = null
        leftNibbleViewed = false
        rightNibbleViewed = false
    }

    /**
     * turn 2 bytes into a unsigned integer
     */
    val uint16 = {
        val intbytes = take(2)
        ISOValue.UInt16(
            intbytes[0],
            intbytes[1]
        )
    }
    val sint16 = {
        val intbytes = take(2)
        ISOValue.SInt16(
            intbytes[0],
            intbytes[1]
        )
    }


    /**
     * turn a byte into an unsigned integer
     */
    val uint8 = {
            ISOValue.UInt8(take(1).first())
    }
    val sint8 = {
        ISOValue.SInt8(take(1).first())
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
    val flag : (Boolean) -> ISOValue.Flag = {
            bool : Boolean ->
        ISOValue.Flag(bool)
    }

    val dateTime : () -> ISOValue.DateTime = {
        ISOValue.DateTime(
            year = uint16(),
            month = uint8(),
            day = uint8(),
            hours = uint8(),
            minutes = uint8(),
            seconds = uint8()
        )
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

    /**
     * assign the bytes in range to the flag field and remove them from the data bytes
     */
    fun flags(range : IntRange) {
        flagBytes = bytes.slice(range).toByteArray()
        if(range.last >= bytes.size) bytes = byteArrayOf()
        bytes = if(range.first >0) bytes.sliceArray(0 until range.first) + bytes.sliceArray(range.last+1 until bytes.size)
        else bytes.sliceArray(range.last+1 until bytes.size)
    }
}