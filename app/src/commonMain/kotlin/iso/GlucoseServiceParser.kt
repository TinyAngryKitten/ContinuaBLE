package iso

import bledata.BLEReading
import bledata.GlucoseFeatures
import data.BloodGlucoseMeasurement
import data.DataRecord
import data.EmptyRecord
import data.GlucoseRecord
import sample.logger
import util.parseUInt16
import util.positiveBitAt


private var glucoseFeatures = mapOf<String,GlucoseFeatures>()

//verbose version of parseGlucoseReading
/*
fun parseGlucoseReading(reading : BLEReading) : DataRecord {
    if(reading.data.size < 10) {
        logger.debug("glucose measurement is missing required bytes")
        return EmptyRecord
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

        return GlucoseRecord(
            glucoseUnitFromFlag(flags.concentrationUnit),
            glucose,
            sequenceNumber,
            null
        )
    }

    logger.error("glucose measurement received without concentration, an error likely occured.")
    return EmptyRecord
}*/

fun parseGlucoseReading(reading : BLEReading) : DataRecord =
    parse(reading.data) {
        flags(0..1)

        GlucoseRecord.fromISOValues(
            unit = boolean( flag(2) ),
            sequenceNumber = uint16(),
            //ignore date and time of measurement
            amount = dropThen(8) {onCondition(flag(1), sfloat)},
            context = null
        ) ?: EmptyRecord
    }

fun parseGlucoseContextReading() : DataRecord = EmptyRecord


/**
 * Glucose features should be saved for each device
 * because it describes how measurements should be parsed
 *
 * always returns a empty reading because the information is only useful for this class
 */
fun parseGlucoseFeatures(reading : BLEReading) : DataRecord =
    parse(reading.data) {
        flags(0..2)

        //idk what to do with this information
        val features = GlucoseFeatures(
            flag(0),
            flag(1),
            flag(2),
            flag(3),
            flag(4),
            flag(5),
            flag(6),
            flag(7),
            flag(8),
            flag(9),
            flag(10)
        )

        EmptyRecord
    }