package iso.services

import bledata.BLEReading
import data.HeartRateRecord
import data.SensorContact
import iso.ISOValue
import iso.parse

fun parseHeartRateMeasurement(reading: BLEReading) =
    parse(reading) {
        flags(0..0)

        HeartRateRecord(
            //type of measurement can vary, if uint8 just turn it into a uint16
            if(flag(0)) uint16() else ISOValue.UInt16(uint8().value),
            onCondition(flag(3),uint16),
            parseSensorContact(flag(1),flag(2)),
            null,
            reading.device
        )
    }

private fun parseSensorContact(flag1 : Boolean, flag2 : Boolean) =
    if(!flag1) SensorContact.NotSupported
    else if(flag2) SensorContact.ContactDetected
    else SensorContact.ContactNotDetected