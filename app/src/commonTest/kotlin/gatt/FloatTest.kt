package gatt

import util.toByteArray
import kotlin.test.Test

class FloatTest{
    @Test
    fun `parse Float with last mantissabyte equal one and zero exponent`() {
        val float = ISOParser(toByteArray(listOf("80", "03"))).sfloat()
    }
}