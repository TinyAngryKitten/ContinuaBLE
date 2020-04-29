package ble

import bledata.BLEReading
import bledata.BLEState
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID

interface BleCentralInterface {
    fun scanForDevices()

    fun connectToDevice(deviceDescription: PeripheralDescription)

    fun bleState(): BLEState

    fun changeStateChangeCallback(callback : (BLEState) -> Unit)

    fun changeResultCallback(callback : (BLEReading) -> Unit)

    fun changeOnDiscoverCallback(callback: (PeripheralDescription)->Unit)

    fun changeOnConnectCallback(callback : (PeripheralDescription)->Unit)

    fun changeOnCharacteristicDiscovered(callback: (PeripheralDescription,CharacteristicUUIDs,ServiceUUID)-> Unit)
}