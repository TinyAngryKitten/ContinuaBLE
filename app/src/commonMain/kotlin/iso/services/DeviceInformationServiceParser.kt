package iso.services

import bledata.BLEReading
import data.DeviceInfo
import iso.ISOParser
import iso.ISOValue
import iso.parse

fun parseModelNumber(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.ModelNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSerialNumber(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.SerialNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseFirmwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.FirmwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseHardwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.HardwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSoftwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.SoftwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseManufacturerName(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfo.ManufacturerName(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }
