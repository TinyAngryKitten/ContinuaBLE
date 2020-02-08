package iso.services

import bledata.BLEReading
import data.*
import iso.parse

fun parseTemperatureMeasurement(reading : BLEReading) =
    parse(reading) {
        flags(0..0)

        ThermometerMeasurement(
            measurementValue = float(),
            timeStamp = onCondition(flag(1), dateTime),
            measurementUnit = if(flag(0)) TemperatureUnit.Fahrenheit else TemperatureUnit.Celsius,
            temperatureType = onCondition(flag(2),sint8)?.let {
                TemperatureType.fromInt(it.value)
            },
            device = reading.device
        )
    }

