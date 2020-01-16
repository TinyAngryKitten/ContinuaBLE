package iso.services

import bledata.BLEReading
import data.*
import iso.ISOValue
import iso.parse
import util.leftMostNibble
import util.rightMostNibble


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

fun parseGlucoseContextReading(reading: BLEReading) : DataRecord =
    parse(reading.data) {
        flags(0..1)

        var tester : ISOValue.UInt8? = null
        var health : ISOValue.UInt8? = null

        GlucoseRecordContext.fromISOValues(
            sequenceNumber = uint16(),
            carbohydrateType = onCondition(flag(0),uint8),
            mealWeight = onCondition(flag(0),sfloat),
            mealContext = onCondition(flag(1),uint8),

            //a wonky way to deal with nibbles, should be implemented in ISOParser, but not worth the work for a single use case
            tester = onCondition(flag(2)) {
                val byte = uint8()
                tester = ISOValue.UInt8(byte.value.toByte().rightMostNibble().toUInt())
                health = ISOValue.UInt8(byte.value.toByte().leftMostNibble().toUInt())
                tester
            },
            health = onCondition(flag(2)) {health},

            exerciseDuration = onCondition(flag(3),uint16),
            exerciseIntensityPercent = onCondition(flag(3),uint8),

            medicationID = onCondition(flag(4),uint8),
            medicationInKg = onCondition(flag(4) && !flag(5),sfloat),
            medicationInLiter = onCondition(flag(4) && flag(5),sfloat),
            HbA1cPercent = onCondition(flag(6),sfloat)
        ) ?: EmptyRecord
    }

fun parseGlucoseFeatures(reading : BLEReading) =
    parse(reading.data) {
        flags(0..2)

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