package gatt.services

import bledata.BLEReading
import data.*
import data.glucose.RecordControlPointResponse
import gatt.GATTValue
import gatt.parse
import util.leftMostNibble
import util.rightMostNibble


fun parseGlucoseReading(reading : BLEReading) : DataRecord =
    parse(reading) {
        flags(8)

        GlucoseRecord.fromISOValues(
            unit = flag(flag(2)),
            sequenceNumber = uint16(),
            //ignore date and time of measurement
            baseTime = dateTime(),
            timeOffset = onCondition(flag(0),sint16),
            amount = onCondition(flag(1),sfloat),
            sampleType = onCondition(flag(1),rightNibble),
            sampleLocation = onCondition(flag(1),leftNibble),
            sensorStatusAnunciation = onCondition(flag(3),uint16),
            contextFollows = flag(4),
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }

fun parseGlucoseContextReading(reading: BLEReading) : DataRecord =
    parse(reading) {
        flags(0..0)

        var tester : GATTValue.UInt8? = null
        var health : GATTValue.UInt8? = null

        GlucoseRecordContext.fromISOValues(
            sequenceNumber = uint16(),
            carbohydrateType = onCondition(flag(0),uint8),
            mealWeight = onCondition(flag(0),sfloat),
            mealContext = onCondition(flag(1),uint8),

            //a wonky way to deal with nibbles, should be implemented in ISOParser, but not worth the work for a single use case
            tester = onCondition(flag(2)) {
                val byte = uint8()
                tester = GATTValue.UInt8(byte.value.toByte().rightMostNibble().toByte())
                health = GATTValue.UInt8(byte.value.toByte().leftMostNibble().toByte())
                tester
            },
            health = onCondition(flag(2)) {health},

            exerciseDuration = onCondition(flag(3),uint16),
            exerciseIntensityPercent = onCondition(flag(3),uint8),

            medicationID = onCondition(flag(4),uint8),
            medicationInKg = onCondition(flag(4) && !flag(5),sfloat),
            medicationInLiter = onCondition(flag(4) && flag(5),sfloat),
            HbA1cPercent = onCondition(flag(6),sfloat),
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }

fun parseGlucoseFeatures(reading : BLEReading) =
    parse(reading) {
        flags(0..1)

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
            flag(10),
            reading.device
        )
    }

fun parseRecordControlPoint(reading : BLEReading) =
    parse(reading) {
        ControlPointRecord(
            RecordControlPointResponse.fromInt(
                uint8().value
            ),
            reading.device
        )
    }