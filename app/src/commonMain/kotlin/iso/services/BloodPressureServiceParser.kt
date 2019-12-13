package iso.services

import bledata.BLEReading
import data.BloodPressureFeatures
import data.BloodPressureRecord
import data.BloodPressureUnit
import data.EmptyRecord
import iso.parse


fun bloodPressureFeatureParser(reading : BLEReading) =
    parse(reading.data) {
        flags(0..2)

        BloodPressureFeatures(
            flag(0),
            flag(1),
            flag(2),
            flag(3),
            flag(4),
            flag(5)
        )

        EmptyRecord
    }

fun bloodPressureMeasurementParser(reading : BLEReading) =
    parse(reading.data) {
        flags(0..1)

        BloodPressureRecord.fromISO(
            systolic = sfloat(),
            diastolic = sfloat(),
            meanArtieralPressure = sfloat(),
            timeStamp = onCondition( flag(1), dateTime),
            unit = if(flag(0)) BloodPressureUnit.kPa else BloodPressureUnit.mmHg,
            bpm = onCondition(flag(2), sfloat),
            userId = onCondition(flag(3),uint8),
            status = null//onCondition(flag(4), ISOValue.Flags())
        ) ?: EmptyRecord
    }

//TODO: find out if this is something i should support
/*
fun intermediateCuffPressureParser(reading : BLEReading) =
    parse(reading.data) {

    }*/