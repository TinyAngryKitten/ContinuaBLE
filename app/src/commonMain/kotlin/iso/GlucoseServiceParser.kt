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

fun parseGlucoseReading(reading : BLEReading) : DataSample {
    var unparsedBytes = reading.data

    logger.debug("glucose:" + parseUInt16(unparsedBytes[11],unparsedBytes[12]).toString())

    //  logger.error("glucose measurement received without concentration, an error likely occured.")
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
        val flagByte = reading.data[0]

        logger.debug("parsing reading: $reading")

        val features = GlucoseFeatures(
            flagByte.positiveBitAt(0),
            flagByte.positiveBitAt(1),
            flagByte.positiveBitAt(2),
            flagByte.positiveBitAt(3),
            flagByte.positiveBitAt(4),
            flagByte.positiveBitAt(5),
            flagByte.positiveBitAt(6),
            flagByte.positiveBitAt(7),
            flagByte.positiveBitAt(8),
            flagByte.positiveBitAt(9),
            flagByte.positiveBitAt(10)
        )

        logger.debug("features of newly connected devices: \n" + features.toString())
    }
    return EmptyReading
}