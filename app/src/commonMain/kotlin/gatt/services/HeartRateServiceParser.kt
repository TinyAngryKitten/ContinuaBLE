package gatt.services

import bledata.BLEReading
import data.HeartRateRecord
import data.SensorContact
import gatt.GATTValue
import gatt.parse

fun parseHeartRateMeasurement(reading: BLEReading) =
    parse(reading) {
        flags(0..0)

        HeartRateRecord(
            //type of measurement can vary, if uint8 just turn it into a uint16
            measurementValue = if(flag(0)) uint16() else GATTValue.UInt16(uint8().byte,0.toByte()),
            energyExpended = onCondition(flag(3),uint16),
            sensorContact = parseSensorContact(flag(1),flag(2)),
            rrInterval = if(flag(4)) (0 until bytes.size/2).map {uint16()} else null,
            device = reading.device
        )
    }

private fun parseSensorContact(flag1 : Boolean, flag2 : Boolean) =
    if(!flag1) SensorContact.NotSupported
    else if(flag2) SensorContact.ContactDetected
    else SensorContact.ContactNotDetected