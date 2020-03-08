package iso.services

import bledata.BLEReading
import data.BloodPressureFeatures
import data.BloodPressureRecord
import data.BloodPressureUnit
import data.EmptyRecord
import iso.parse


fun parseBloodPressureFeature(reading : BLEReading) =
    parse(reading) {
        flags(0..1)

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
        flags(0..0)

        BloodPressureRecord.finalFromISO(
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

fun intermediateCuffPressureParser(reading : BLEReading) =
    parse(reading) {
        BloodPressureRecord.intermediateFromISO(
            systolic = sfloat(),
            timeStamp = onCondition( flag(1), dateTime),
            unit = if(flag(0)) BloodPressureUnit.kPa else BloodPressureUnit.mmHg,
            bpm = onCondition(flag(2), sfloat),
            userId = onCondition(flag(3),uint8),
            status = null,//onCondition(flag(4), ISOValue.Flags())
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }