package ble

import bledata.BLEReading
import bledata.BLEState
import data.PeripheralDescription


expect class BLECentral{

    fun scanForDevices()

    fun connectToDevice(deviceDescription: PeripheralDescription)

    fun bleState(): BLEState

    fun changeStateChangeCallback(callback : (BLEState) -> Unit)

    fun changeResultCallback(callback : (BLEReading) -> Unit)

    fun changeOnDiscoverCallback(callback: (PeripheralDescription)->Unit)

    fun changeOnConnectCallback(callback : (PeripheralDescription)->Unit)
}
