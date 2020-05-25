package gatt.services

import bledata.BLEReading
import data.DeviceInfoComponent
import gatt.parse

fun parseModelNumber(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.ModelNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSerialNumber(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.SerialNumber(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseFirmwareRevision(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.FirmwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseHardwareRevision(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.HardwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseSoftwareRevision(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.SoftwareRevision(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }

fun parseManufacturerName(reading : BLEReading) =
    parse(reading) {
        DeviceInfoComponent.ManufacturerName(
            value = utf8(reading.data.size).encodedString,
            device = reading.device
        )
    }
