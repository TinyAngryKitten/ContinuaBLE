package ble

import bledata.BLEReading
import data.EmptyRecord
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBPeripheral
import platform.Foundation.NSData
import platform.Foundation.base64Encoding
import util.toHexString
class BleMessageParser {
    fun packageBleReading(data : NSData?, device: CBPeripheral?,characteristic: CBCharacteristic?) : BLEReading =
        BLEReading(
            PeripheralDescription.fromNullable(
                device?.identifier?.UUIDString ?: "",
                device?.name
            ),
            CharacteristicUUIDs.fromNr(characteristic?.UUID?.UUIDString ?: "0000"),
            nsDataToByteArray(data)
        )

    private fun nsDataToByteArray(data : NSData?) : ByteArray =
        data
            ?.bytes
            ?.reinterpret<ByteVar>()
            ?.readBytes(data.length.toInt()) ?: ByteArray(0)
}

fun packageBleReading(data : NSData?, device: CBPeripheral,characteristic: CBCharacteristic) : BLEReading =
    BLEReading(
        PeripheralDescription.fromNullable(
            device.identifier.UUIDString,
            device.name
        ),
        CharacteristicUUIDs.fromNr(characteristic.UUID.UUIDString),
        nsDataToByteArray(data)
    )

private fun nsDataToByteArray(data : NSData?) : ByteArray =
    data
        ?.bytes
        ?.reinterpret<ByteVar>()
        ?.readBytes(data.length.toInt()) ?: ByteArray(0)