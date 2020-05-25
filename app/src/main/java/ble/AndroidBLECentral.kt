package ble

import android.bluetooth.BluetoothAdapter
import bledata.BLEReading
import bledata.BLEState
import bledata.PeripheralDescription
import gatt.CharacteristicUUIDs
import gatt.ServiceUUID

class AndroidBLECentral(val controller : BluetoothController ) : BleCentralInterface, BleCentralCallbackInterface by controller {

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
}