package iso

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class SIntTest {
    //SInt8
    @Test
    fun `parse SInt8 with negative int results in correct value`() {
        val integerValue = "FF"
        val result = ISOParser(toByteArray(listOf(integerValue))).sint8()
        assertEquals(-1,result.value)
    }
    @Test
    fun `parse SInt8 with zero int results in correct value`() {
        val integerValue = "0"
        val result = ISOParser(toByteArray(listOf(integerValue))).sint8()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse SInt8 with a positive value results in correct value`() {
        val integerValue = "7F"
        val result = ISOParser(toByteArray(listOf(integerValue))).sint8()
        assertEquals(integerValue.toInt(16),result.value)
    }

    //SInt16
    @Test
    fun `parse SInt16 with a positive bytes results in correct value`() {
        val integerValue = "7F30"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).sint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse SInt16 with a positive byte1 and negative byte2 results in correct value`() {
        val integerValue = "FF7F"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).sint16()
        assertEquals(-129,result.value)
    }
    @Test
    fun `parse SInt16 with a negative byte1 and positive byte2 results in correct value`() {
        val integerValue = "7FFF"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).sint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse SInt16 with negative bytes results in correct value`() {
        val integerValue = "FFFF"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).sint16()
        assertEquals(-1,result.value)
    }
    @Test
    fun `parse SInt16 with zero bytes results in correct value`() {
        val integerValue = "0000"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).sint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
}