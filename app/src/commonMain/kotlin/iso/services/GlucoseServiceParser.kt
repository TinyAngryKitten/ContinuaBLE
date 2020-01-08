package iso.services

import bledata.BLEReading
import data.DataRecord
import data.EmptyRecord
import data.GlucoseFeatures
import data.GlucoseRecord
import iso.parse



fun parseGlucoseReading(reading : BLEReading) : DataRecord =
    parse(reading.data) {
        flags(0..1)

        GlucoseRecord.fromISOValues(
            unit = flag(flag(2)),
            sequenceNumber = uint16(),
            //ignore date and time of measurement
            amount = dropThen(8) { onCondition(flag(1), sfloat) },
            context = null
        ) ?: EmptyRecord
    }

fun parseGlucoseContextReading(reading: BLEReading) : DataRecord = EmptyRecord


/**
 * Glucose features should be saved for each device
 * because it describes how measurements should be parsed
 *
 * always returns a empty reading because the information is only useful for this class
 */
fun parseGlucoseFeatures(reading : BLEReading) =
    parse(reading.data) {
        flags(0..2)

        //idk what to do with this information
        GlucoseFeatures(
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
    }