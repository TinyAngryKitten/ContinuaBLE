package ble

import android.bluetooth.BluetoothAdapter
import bledata.BLEReading
import bledata.BLEState
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID

actual class BLECentral(val controller : BluetoothController ) : BleCentralInterface{

    val discoveredDevices : List<PeripheralDescription>
        get() = controller.discoveredDevices.map { PeripheralDescription(it.address,it.name) }

    val connectedDevices : List<PeripheralDescription>
        get() = controller.connectedDevices.map { PeripheralDescription(it.address,it.name) }

     override fun scanForDevices(){
        controller.scan()
    }

    override fun connectToDevice(deviceDescription: PeripheralDescription) {
        val device = controller.discoveredDevices
            .find { it.address.equals(deviceDescription.UUID,ignoreCase = true) } ?: return

        controller.connectToDevice(device.address)
    }


    override fun bleState(): BLEState {
        if(!(controller.adapter).isEnabled) BLEState.NotAuthorized
        return when(controller.adapter.state) {
            BluetoothAdapter.STATE_ON -> BLEState.On
            BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF,BluetoothAdapter.STATE_TURNING_ON-> BLEState.Off
            else -> BLEState.UnknownErrorState
        }
    }

    override fun changeResultCallback(callback: (BLEReading) -> Unit) {
        controller.resultCallback.set(callback)
    }

    override fun changeOnDiscoverCallback(callback: (PeripheralDescription) -> Unit) {
        controller.discoverCallback.set(callback)
    }

    override fun changeOnConnectCallback(callback: (PeripheralDescription) -> Unit) {
        controller.connectCallback.set(callback)
    }

    override fun changeOnCharacteristicDiscovered(callback: (PeripheralDescription, CharacteristicUUIDs, ServiceUUID) -> Unit) {
        controller.characteristicDiscoveredCallback.set(callback)
    }

    override fun changeStateChangeCallback(callback: (BLEState) -> Unit) {
        controller.stateChangedCallback.set(callback)
    }


}