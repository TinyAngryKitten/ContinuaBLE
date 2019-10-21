package iso

import bledata.BLEReading
import bledata.GlucoseFeatures
import data.BloodGlucoseMeasurement
import data.DataSample
import data.EmptyReading
import data.GlucoseReading
import sample.logger
import util.parseUInt16
import util.positiveBitAt
import util.readValueInRange
import util.strRepresentation


private var glucoseFeatures = mapOf<String,GlucoseFeatures>()

//TODO: cleanup
fun parseGlucoseReading(reading : BLEReading) : DataSample {
    if(reading.data.size < 10) {
        logger.debug("glucose measurement is missing required bytes")
        return EmptyReading
    }

    var unparsedBytes = reading.data
    val flags = parseMeasurementFlags(unparsedBytes.get(0))
    unparsedBytes = unparsedBytes.sliceArray(1..unparsedBytes.size-1)
    println("flags: $flags")

    val sequenceNumber = parseUInt16(unparsedBytes[0],unparsedBytes[1])
    unparsedBytes = unparsedBytes.sliceArray(2..unparsedBytes.size-1)
    println("sequence number: $sequenceNumber")

    //cant parse date_time yet
    unparsedBytes = unparsedBytes.sliceArray(7..unparsedBytes.size-1)

    //same for offset time
    if(flags.timeOffset) unparsedBytes = unparsedBytes.sliceArray(2..unparsedBytes.size-1)

    if(flags.concentrationTypeAndSample) {
        val glucose = SFloat.fromBytes(unparsedBytes[0],unparsedBytes[1])
        unparsedBytes = unparsedBytes.sliceArray(2..unparsedBytes.size-1)

        if (glucose is SFloat.Value) logger.debug("glucose concentration: " + glucose.floatValue)
        else logger.debug("error: $glucose")

        return GlucoseReading(
            glucoseUnitFromFlag(flags.concentrationUnit),
            glucose,
            sequenceNumber,
            null
        )
    }

    logger.error("glucose measurement received without concentration, an error likely occured.")
    return EmptyReading
}

private fun glucoseUnitFromFlag(flag :Boolean) = if(flag) BloodGlucoseMeasurement.MMOL else BloodGlucoseMeasurement.DL

private fun parseMeasurementFlags(byte : Byte) : GlucoseMeasurementFlags =
    GlucoseMeasurementFlags(
        byte.positiveBitAt(0),
        byte.positiveBitAt(1),
        byte.positiveBitAt(2),
        byte.positiveBitAt(3),
        byte.positiveBitAt(4)
    )

/**
 * Glucose features should be saved for each device
 * because it describes how measurements should be parsed
 *
 * always returns a empty reading because the information is only useful for this class
 */
fun parseGlucoseFeatures(reading : BLEReading) : DataSample {
    if(reading.data.size < 1) {
        logger.error("attempted to parse glucose features with empty data field: $reading")
    } else {
        val flagByte1 = reading.data[0]
        val flagByte2 = reading.data[1]


        logger.debug("parsing reading: $reading")

        val features = GlucoseFeatures(
            flagByte1.positiveBitAt(0),
            flagByte1.positiveBitAt(1),
            flagByte1.positiveBitAt(2),
            flagByte1.positiveBitAt(3),
            flagByte1.positiveBitAt(4),
            flagByte1.positiveBitAt(5),
            flagByte1.positiveBitAt(6),
            flagByte1.positiveBitAt(7),
            flagByte2.positiveBitAt(0),
            flagByte2.positiveBitAt(1),
            flagByte2.positiveBitAt(2)
        )

        logger.debug("features of newly connected device: \n" + features.toString())
    }
    return EmptyReading
}