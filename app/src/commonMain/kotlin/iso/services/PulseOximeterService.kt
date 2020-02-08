package iso.services

import bledata.BLEReading
import data.*
import iso.ISOValue
import iso.parse

//TODO: implement feature parsing(SO MANY FIELDS)....
/*fun parsePulseOximeterFeatures(reading: BLEReading) =
    parse(reading) {
        flags(0..6)
        PulseOximeterFeatures(

        )
    }*/

fun parsePlxSpotCheck(reading:  BLEReading) =
    parse(reading) {
        flags(0..0)
        PLXSpotCheck.fromISO(
            spo2 = sfloat(),
            PR = sfloat(),
            timeStamp = onCondition(flag(0),dateTime),
            measurementStatus = onCondition(flag(1),sint16),
            sensorstatus1 = onCondition(flag(2),sint16),
            sensorstatus2 = onCondition(flag(2),sint8),
            pulseAmplitudeIndex = onCondition(flag(3),sfloat),
            device = reading.device

        )?: EmptyRecord(reading.device)
    }

fun parseContinousPlxMeasurement(reading:  BLEReading) =
    parse(reading) {
        flags(0..0)
        PLXContinousMeasurement.fromISO(
            spo2Normal= sfloat(),
            PRNormal= sfloat(),
            spo2Fast = onCondition(flag(0), sfloat),
            PRFast = onCondition(flag(0), sfloat),
            spo2Slow = onCondition(flag(1), sfloat),
            PRSlow = onCondition(flag(1), sfloat),
            measurementStatus = onCondition(flag(2),sint16),
            sensorstatus1 = onCondition(flag(3),sint16),
            sensorstatus2 = onCondition(flag(3),sint8),
            pulseAmplitudeIndex = onCondition(flag(4),sfloat),
            device = reading.device
        )?: EmptyRecord(reading.device)
    }
