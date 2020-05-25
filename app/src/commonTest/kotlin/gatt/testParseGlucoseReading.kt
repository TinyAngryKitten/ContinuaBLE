package gatt

class TestParseGlucoseReading {

    //                 type  loc  exp  mantissa              time n shit                                                  sequence nr
    //0000000000000000 1111 1000 1011 000001011001
    val glucoseAmountWithDecimalPoint = listOf("F0","31","80","00","00","00","00","00","00","00","00","00","03","87").map {it.toInt(16)}

    /*
    exponent  mantissa                                                                          time  sequence   flags
    0000 0000 00010101 10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000111 0000110
     */

    /*
    val validStrRepresentation = "0000000000010101100000000000000000000000000000000000000000000000000000000000000000000000000000000000001110000111"

    val zeroExponentHex = listOf("00","15","80","00","00","00","00","00","00","00","00","00","03","87").map {it.toInt(16)}
    val noneZeroExponentHex = listOf("30","15","80","00","00","00","00","00","00","00","00","00","03","87").map {it.toInt(16)}

    val exponent = 3

    val sequenceNr = 3
    val mantissa = 21f

    val negativeMantissaReading = BLEReading(
        PeripheralDescription(""),
        CharacteristicUUIDs.glucoseMeasurement,
        ByteArray(glucoseAmountWithDecimalPoint.size) {
            i->
            glucoseAmountWithDecimalPoint[glucoseAmountWithDecimalPoint.size-(i+1)].toUByte().toByte()
        }
    )


    val zeroExponentReading = BLEReading(
        PeripheralDescription(""),
        CharacteristicUUIDs.glucoseMeasurement,
        //"1580000000000000000386".toInt(16)
        ByteArray(zeroExponentHex.size) {
            i ->
            zeroExponentHex[zeroExponentHex.size - (i+1)].toUByte().toByte()
        }

    )

    val noneZeroExponentReading = BLEReading(
        PeripheralDescription(""),
        CharacteristicUUIDs.glucoseMeasurement,
        //"1580000000000000000386".toInt(16)
        ByteArray(noneZeroExponentHex.size) {
                i ->
            noneZeroExponentHex[noneZeroExponentHex.size - (i+1)].toUByte().toByte()
        }

    )

    //TODO: Better tests

    @Test
    fun testValidStrRepresentation() {
        assertEquals(zeroExponentReading.data.strRepresentation(),validStrRepresentation)
    }

    @Test
    fun testValidReadingWithNegativeMantissa() {
        val result = parseGlucoseReading(negativeMantissaReading)
        assertTrue(result is GlucoseRecord,"result lass : ${result::class}")
        assertEquals(sequenceNr,result.sequenceNumber)
        assertTrue(result.amount is ISOValue.SFloat.Value)
        //assertEquals(4.9f,result.value)
    }

    @Test
    fun testValidReadingWithZeroExponent() {
        val result = parseGlucoseReading(zeroExponentReading)
        assertTrue(result is GlucoseRecord)
        assertEquals(sequenceNr,result.sequenceNumber)
        //assertEquals(mantissa,result.amount)
    }

    @Test
    fun testValidReadingWithNoneZeroExponent() {
        val result = parseGlucoseReading(noneZeroExponentReading)
        assertTrue(result is GlucoseRecord)
        assertEquals(sequenceNr,result.sequenceNumber)
        //assertEquals(10f.pow(exponent) * mantissa,result.amount)
    }


    @Test
    fun attemptToParseValidGlucoseFeatures() {
        val result = parseBLEReading(
            BLEReading(
                PeripheralDescription(""),
                CharacteristicUUIDs.glucoseFeature,
                ByteArray(
                2
                ) {
                    255.toUByte().toByte()
                }
            )
        )
        assertTrue(result is GlucoseFeatures)
        assertTrue(result.timeFault)
    }

    @Test
    fun parseDeviceInfo() {
        val bytes = listOf("36","2E","38","2E","31","76").map {it.toInt(16).toUByte().toByte()}
        val utf = ISOValue.UTF8(ByteArray(bytes.size) {
            i-> bytes[i]
        })

        print("STR: ${utf.encodedString}")
    }*/
}