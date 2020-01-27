package iso.services

import bledata.BLEReading
import data.BodyCompositionFeature
import data.BodyCompositionRecord
import iso.ISOValue
import iso.parse

fun parseBodyCompositionFeature(reading : BLEReading) =
    parse(reading) {
        flags(0..3)
        BodyCompositionFeature(
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
            weightResolution(11),
            heightResolution(15),
            reading.device
        )
    }

fun parseBodyCompositionMeasurement(reading: BLEReading) =
    parse(reading) {
        flags(0..1)

        //compiler error for some reason when using named arguments
        BodyCompositionRecord(
            uint16(),
            dateTime(),
            uint8(),
            uint16(),
            uint16(),
            uint16(),
            uint16(),
            uint16(),
            uint16(),
            uint16(),
            reading.device
        )
    }