package iso.services

import bledata.BLEReading
import data.BatteryLevel
import iso.parse

fun parseBatteryLevel(reading : BLEReading) = parse(reading.data){
    BatteryLevel(
        uint8(),
        reading.device
    )
}