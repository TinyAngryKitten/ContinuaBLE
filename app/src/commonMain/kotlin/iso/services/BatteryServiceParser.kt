package iso.services

import bledata.BLEReading
import data.BatteryLevelRecord
import iso.parse

fun parseBatteryLevel(reading : BLEReading) = parse(reading){
    BatteryLevelRecord(
        uint8(),
        reading.device
    )
}