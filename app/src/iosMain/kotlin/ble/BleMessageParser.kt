package ble

import bledata.BLEReading
import data.CharacteristicDescription
import data.PeripheralDescription
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBPeripheral
import platform.Foundation.NSData
import platform.Foundation.base64Encoding
import util.toHexString

fun packageBleReading(data : NSData?, device: CBPeripheral,characteristic: CBCharacteristic) : BLEReading =
    BLEReading(
        PeripheralDescription.fromNullable(
            device.identifier.UUIDString,
            device.name
        ),
        CharacteristicDescription(
            characteristic.UUID.UUIDString
        ),
        nsDataToByteArray(data)
    )

private fun nsDataToByteArray(data : NSData?) : ByteArray =
    data
        ?.bytes
        ?.reinterpret<ByteVar>()
        ?.readBytes(data.length.toInt()) ?: ByteArray(0)