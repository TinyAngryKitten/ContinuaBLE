package gatt

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SFloatTest {
    @Test
    fun `parse SFLoat with last mantissabyte equal one and zero exponent`() {
        val float = ISOParser(toByteArray(listOf("00", "8C"))).sfloat()
        assertTrue(float is GATTValue.SFloat.Value)
        assertEquals(140f, float.value)
    }

    @Test
    fun `parse SFLoat with mantissa over 8 bit and zero exponent`() {
        val float = ISOParser(toByteArray(listOf("02", "E7"))).sfloat()
        assertTrue(float is GATTValue.SFloat.Value)
        assertEquals(743f, float.value)
    }

    @Test//exponent = 3, mantissa = 240
    fun `parse SFLoat with nonzero exponent`() {
        val float = ISOParser(toByteArray(listOf("30", "F0"))).sfloat()
        assertTrue(float is GATTValue.SFloat.Value)
        assertEquals(240000f, float.value)
    }
}