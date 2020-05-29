package ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import bledata.BLEReading
import bledata.PeripheralDescription
import gatt.CharacteristicUUIDs
import gatt.identifier

fun packageBleReading(data : ByteArray?, device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) : BLEReading =
    BLEReading(
        PeripheralDescription.fromNullable(
            device.address,
            device.name
        ),
        CharacteristicUUIDs.fromNr(characteristic.uuid.identifier),
        data?: ByteArray(0)
    )