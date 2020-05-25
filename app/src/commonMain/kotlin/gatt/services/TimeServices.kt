package gatt.services

import bledata.*
import data.CurrentTimeRecord
import data.DateTimeRecord
import gatt.parse

fun parseDateTime(reading: BLEReading) = parse(reading) {
    DateTimeRecord(
        dateTime(),
        reading.device
    )
}

fun parseCurrentTime(reading: BLEReading) = parse(reading) {
    flags(9..9)

    CurrentTimeRecord(

        CurrentTime(
            ExactTime(
                DayDateTime(
                    dateTime(),
                    DayOfWeekEnum.fromUInt8(uint8())
                ),
                uint8()
            ),

            AdjustReason(
                flag(0),
                flag(1),
                flag(2),
                flag(3)
            )
        ),

    reading.device

    )
}