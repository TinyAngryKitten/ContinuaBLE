package gatt.services

import bledata.BLEReading
import data.BodySensorLocation
import data.BodySensorLocationRecord
import gatt.parse

fun parseBodySensorLocation(reading : BLEReading) =
    parse(reading){
        BodySensorLocationRecord(

            when(uint8().value.toInt()) {
                1 -> BodySensorLocation.Chest
                2 -> BodySensorLocation.Wrist
                3 -> BodySensorLocation.Finger
                4 -> BodySensorLocation.Hand
                5 -> BodySensorLocation.EarLobe
                6 -> BodySensorLocation.Foot
                else -> BodySensorLocation.Other
            },
            reading.device
        )
    }