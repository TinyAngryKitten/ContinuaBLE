package iso

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class NibbleTest {
    //Nibbles
    @Test
    fun `parse left nibble of negative value stays negative`() {
        val integerValue = "A7"//1010 0111
        val result = ISOParser(toByteArray(listOf(integerValue))).leftNibble()
        assertEquals(-6,result.value)
    }
    @Test
    fun `parse left nibble of positive value stays positive`() {
        val integerValue = "40"//0100 0000
        val result = ISOParser(toByteArray(listOf(integerValue))).leftNibble()
        assertEquals(4,result.value)
    }
    @Test
    fun `parse right nibble of negative value is not negative`() {
        val integerValue = "F3"//1111 0011
        val result = ISOParser(toByteArray(listOf(integerValue))).rightNibble()
        assertEquals(3,result.value)
    }
    @Test
    fun `parse right nibble of positive value is not positive`() {
        val integerValue = "4F"//0100 1111
        val result = ISOParser(toByteArray(listOf(integerValue))).rightNibble()
        assertEquals(-1,result.value)
    }

    @Test
    fun testToUnsignedRightNibble() {
        val integerValue = "F8"//1111 1000
        val result = ISOParser(toByteArray(listOf(integerValue))).rightNibble()
        assertEquals(8,result.unsigned)
    }

    @Test
    fun testToUnsignedLeftNibble() {
        val integerValue = "F8"//1111 1000
        val result = ISOParser(toByteArray(listOf(integerValue))).leftNibble()
        assertEquals(15,result.unsigned)
    }
}