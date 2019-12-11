package util

import iso.FlagPosition
import iso.MessagePosition
import iso.ValuePosition
import kotlin.experimental.and
import kotlin.math.ceil
import kotlin.math.pow

fun ByteArray.strRepresentation() : String = foldRight("") {
        it:Byte, acc:String ->
    acc + it.strRepresentation()
}

fun Byte.strRepresentation() : String = (7 downTo 0).fold("") {
        acc: String, i:Int ->
    acc + if(positiveBitAt(i)) "1" else "0"
}

fun Byte.bitAt(index : Int) = and(1.shl(index).toByte())
fun Byte.positiveBitAt(index : Int) = bitAt(index).toInt() != 0


/**
 * @return Returns a hex representation of a collection of bytes,
 * each byte is represented by exactly 2 hexadecimals.
 * returns empty string if null
 */
fun ByteArray?.toHexString() : String {
    val strBuilder      = StringBuilder()
    this?.forEach {
        val hexRepresentation = it.toString(16)

        if(hexRepresentation.length == 1) strBuilder.append("0"+hexRepresentation)
        else strBuilder.append(hexRepresentation)
    }
    return strBuilder.toString()
}

fun ByteArray.readFlagAt(position : FlagPosition) : Boolean =
    get(position.bytePosition)
        .positiveBitAt(position.bitPosition)

fun ByteArray.readValueInRange(position : ValuePosition) : ByteArray =
    sliceArray(position.getRangeOfBytes())
        .updateLast { it.and(Byte.fill(1)) }

fun Byte.Companion.fill(nrOfBits : Int) : Byte = ( 2f.pow(nrOfBits%9) -1).toByte()

fun readBitsOfByteArray(array : ByteArray,position : ValuePosition) : String {
    val bytes = array.sliceArray(IntRange(position.bytePosition, position.bytePosition + position.length)).toHexString()
    return ""
}

fun ByteArray.update(index: Int, newItem: Byte): ByteArray = mapIndexed {
        currentIndex, e ->
    if(index == currentIndex) newItem
    else e
}.toByteArray()

fun ByteArray.update(index: Int, updateFunction: (Byte) -> Byte): ByteArray = mapIndexed {
        currentIndex, e ->
    if(index == currentIndex) updateFunction(e)
    else e
}.toByteArray()

fun ByteArray.updateLast(updateFunction: (Byte) -> Byte) : ByteArray = update(lastIndex,updateFunction)
fun ByteArray.updateLast(newItem: Byte) : ByteArray = update(lastIndex,newItem)

/**
 * Combine two bytes to a integer, does not take 2's compliment into account
 * @param byte1 first 8 bits of the integer
 * param byte2 last 8 bits of the integer
 */
fun parseUInt16(byte1 : Byte, byte2 : Byte) = (0..15).map {
    if(it<8 && byte1.positiveBitAt(it)) 2f.pow(it.toFloat())
    else if(it>=8 && byte2.positiveBitAt(it%8)) 2f.pow(it.toFloat())
    else 0f
}.fold(0u) {acc,v -> acc+v.toUInt()}

fun parseUInt16(byteArray : ByteArray) = parseUInt16(byteArray[0],byteArray[1])

fun Byte.leftMostNibble() = (4..7).fold(0) {
    acc,i ->
    if(positiveBitAt(i)) acc+ 2f.pow(i-4).toInt()
    else acc
}

fun Byte.rightMostNibble() = (0..3).fold(0) {
        acc,i ->
    if(positiveBitAt(i)) acc+ 2f.pow(i).toInt()
    else acc
}

/**
 * Turns a leftmost nible into a signed int,
 * this is needed because negative numbers lose their signs (10100000 become 00001010) etc.
 */
fun nibleToSignedInt(nible : Int) =
    if(nible.toByte().positiveBitAt(3)) nible.xor(-16)
    else nible
