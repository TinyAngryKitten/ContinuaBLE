package iso.services

import bledata.BLEReading
import data.DeviceInfoComponent
import iso.parse

fun parseModelNumber(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.ModelNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSerialNumber(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.SerialNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseFirmwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.FirmwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseHardwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.HardwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSoftwareRevision(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.SoftwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseManufacturerName(reading : BLEReading) =
    parse(reading.data) {
        DeviceInfoComponent.ManufacturerName(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }
