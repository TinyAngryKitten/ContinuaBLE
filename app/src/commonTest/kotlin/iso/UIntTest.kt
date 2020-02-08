package iso

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class UIntTest {
    // UINT 8
    @Test
    fun `parse positive int to uint8 gives returns same value`() {
        val integerValue = "7F"
        val result = ISOParser(toByteArray(listOf(integerValue))).uint8()
        assertEquals(integerValue.toInt(16),result.value)
    }

    @Test
    fun `parse negative int to UInt8 returns positive value`() {
        val integerValue = "FF"
        val result = ISOParser(toByteArray(listOf(integerValue))).uint8()
        assertEquals(integerValue.toInt(16),result.value)
    }

    //UInt 16
    @Test
    fun `parse UInt 16 with negative byte1 positive byte 2 results in correct value`() {
        val integerValue = "7FFF"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).uint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse UInt 16 with positive byte1 negative byte 2 results in correct value`() {
        val integerValue = "FF7F"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).uint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse UInt 16 with all negative bytes results in correct value`() {
        val integerValue = "FFFF"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).uint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
    @Test
    fun `parse UInt 16 with all positive bytes results in correct value`() {
        val integerValue = "4023"
        val result = ISOParser(toByteArray(listOf(integerValue.substring(0,2), integerValue.substring(2)))).uint16()
        assertEquals(integerValue.toInt(16),result.value)
    }
}