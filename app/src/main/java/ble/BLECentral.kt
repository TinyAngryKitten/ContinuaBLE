package ble

import android.bluetooth.BluetoothAdapter
import bledata.BLEReading
import bledata.BLEState
import data.PeripheralDescription

actual class BLECentral(val controller : BluetoothController ) {

    val discoveredDevices : List<PeripheralDescription>
        get() = controller.discoveredDevices.map { PeripheralDescription(it.address,it.name) }

    val connectedDevices : List<PeripheralDescription>
        get() = controller.connectedDevices.map { PeripheralDescription(it.address,it.name) }

     actual fun scanForDevices(){
        controller.scan()
    }

    actual fun connectToDevice(deviceDescription: PeripheralDescription) {
        val device = controller.discoveredDevices
            .find { it.address.equals(deviceDescription.UUID,ignoreCase = true) }

        controller.connectToDevice(device?.address ?: "")
    }


    actual fun bleState(): BLEState {
        if(!(controller.adapter).isEnabled) BLEState.NotAuthorized
        return when(controller.adapter.state) {
            BluetoothAdapter.STATE_ON -> BLEState.On
            BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF,BluetoothAdapter.STATE_TURNING_ON-> BLEState.Off
            else -> BLEState.UnknownErrorState
        }
    }

    actual fun changeResultCallback(callback: (BLEReading) -> Unit) {
        controller.resultCallback.set(callback)
    }

    actual fun changeOnDiscoverCallback(callback: (PeripheralDescription) -> Unit) {
        controller.discoverCallback.set(callback)
    }

    actual fun changeOnConnectCallback(callback: (PeripheralDescription) -> Unit) {
        controller.connectCallback.set(callback)
    }

    actual fun changeStateChangeCallback(callback: (BLEState) -> Unit) {
        //TODO: NOT IMPLEMENTED
    }


}