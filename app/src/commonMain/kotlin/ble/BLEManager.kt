package ble

import data.DeviceInfo
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import kotlin.native.concurrent.SharedImmutable


expect object BLEManager{

    fun scanForDevices()

    //fun subscribeToCharacteristicsOfDevice(deviceDescription: PeripheralDescription, characteristics : List<CharacteristicUUIDs>)

    fun connectToDevice(deviceDescription: PeripheralDescription)
    //fun disconnectFromDevice(deviceDescription: PeripheralDescription)
    fun bleState(): BLEState
}
