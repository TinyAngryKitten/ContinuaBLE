package iso.services

import bledata.BLEReading
import data.EmptyRecord
import data.PeripheralDescription
import iso.parse

fun parseTemperatureMeasurement(reading : BLEReading) =
    parse(reading.data) {
        flags(0..1)

        EmptyRecord(PeripheralDescription("unknown"))
    }