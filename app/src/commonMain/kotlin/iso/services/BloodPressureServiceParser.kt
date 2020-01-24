package iso.services

import bledata.BLEReading
import data.BloodPressureFeatures
import data.BloodPressureRecord
import data.BloodPressureUnit
import data.EmptyRecord
import iso.parse


fun parseBloodPressureFeature(reading : BLEReading) =
    parse(reading) {
        flags(0..2)

        BloodPressureFeatures(
            flag(0),
            flag(1),
            flag(2),
            flag(3),
            flag(4),
            flag(5),
            reading.device
        )
    }

fun parseBloodPressureMeasurement(reading : BLEReading) =
    parse(reading) {
        flags(0..1)

        BloodPressureRecord.fromISOValues(
            systolic = sfloat(),
            diastolic = sfloat(),
            meanArtieralPressure = sfloat(),
            timeStamp = onCondition( flag(1), dateTime),
            unit = if(flag(0)) BloodPressureUnit.kPa else BloodPressureUnit.mmHg,
            bpm = onCondition(flag(2), sfloat),
            userId = onCondition(flag(3),uint8),
            status = null,//onCondition(flag(4), ISOValue.Flags())
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }

//TODO: find out if this is something i should support
/*
fun intermediateCuffPressureParser(reading : BLEReading) =
    parse(reading.data) {

    }*/