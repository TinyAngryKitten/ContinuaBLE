package ble

import bledata.BLEReading
import bledata.BLEState
import bledata.PeripheralDescription
import gatt.CharacteristicUUIDs
import gatt.ServiceUUID

interface BleCentralInterface {
    /**
     * Start to scan for compatible devices in proximity,
     * Discovered devices are sendt to onDiscover callback in device central
     */
    fun scanForDevices()

    /**
     * Attempt to connect to a device, the device must allready be discovered by a scan,
     * else an error will be thrown
     */
    fun connectToDevice(deviceDescription: PeripheralDescription)

    /**
     * returns the state of the Bluetooth adapter
     */
    fun bleState(): BLEState

    //Change the callbacks of events
    fun changeStateChangeCallback(callback : (BLEState) -> Unit)

    fun changeResultCallback(callback : (BLEReading) -> Unit)

    fun changeOnDiscoverCallback(callback: (PeripheralDescription)->Unit)

    fun changeOnConnectCallback(callback : (PeripheralDescription)->Unit)

    fun changeOnCharacteristicDiscovered(callback: (PeripheralDescription, CharacteristicUUIDs, ServiceUUID)-> Unit)
}