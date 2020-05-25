package gatt.services

import bledata.BLEReading
import data.BatteryLevelRecord
import gatt.parse

fun parseBatteryLevel(reading : BLEReading) = parse(reading){
    BatteryLevelRecord(
        uint8(),
        reading.device
    )
}