package gatt.services

import bledata.BLEReading
import data.*
import gatt.GATTValue

fun parseTemperatureMeasurement(reading : BLEReading) =
    reading.parse {
        flags(8)

        ThermometerMeasurement(
            measurementValue = float(),
            timeStamp = requirement {
                flag = 1
                format = dateTime
            },
            measurementUnit = if(flag(0)) TemperatureUnit.Fahrenheit
                                else TemperatureUnit.Celsius,
            temperatureType = requirement<GATTValue.SInt8> {
                flag = 2
                format  = sint8
            }?.let{ TemperatureType.fromInt(it.value)},

            device = reading.device
        )
    }
