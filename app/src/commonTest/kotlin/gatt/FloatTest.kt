package gatt

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloatTest{
    @Test
    fun `parse FLoat with zero exponent`() {
        val float = ISOParser(toByteArray(listOf("00","00", "00", "05"))).float()
        assertEquals(5f, float.value)
    }

    @Test
    fun `parse FLoat with positive mantissa and positive exponent`() {
        val float = ISOParser(toByteArray(listOf("03","02", "02", "E7"))).float()
        assertEquals(131815000f, float.value)
    }

    @Test//exponent = 3, mantissa = 240
    fun `parse FLoat with negative exponent`() {
        val float = ISOParser(toByteArray(listOf("FF","00","00", "23"))).float()
        assertEquals(3.5f, float.value)
    }
}