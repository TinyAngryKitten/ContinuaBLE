package ble

import android.bluetooth.BluetoothAdapter
import data.PeripheralDescription

actual object BLEManager {
    var controller : BluetoothController? = null

    val discoveredDevices : List<PeripheralDescription>
        get() = controller?.discoveredDevices?.map { PeripheralDescription(it.address,it.name) } ?: listOf()

    val connectedDevices : List<PeripheralDescription>
        get() = controller?.connectedDevices?.map { PeripheralDescription(it.address,it.name) } ?: listOf()

     actual fun scanForDevices(){
        controller?.scan()
    }

    actual fun connectToDevice(deviceDescription: PeripheralDescription) {
        val device = controller?.discoveredDevices
            ?.find { it.address.equals(deviceDescription.UUID,ignoreCase = true) }

        controller?.connectToDevice(device?.address ?: "")
    }


    actual fun bleState(): BLEState {
        if(controller == null || controller?.adapter == null) return BLEState.NotSupported
        if((controller?.adapter)?.isEnabled != true) BLEState.NotAuthorized
        return when(controller?.adapter?.state) {
            BluetoothAdapter.STATE_ON -> BLEState.On
            BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF,BluetoothAdapter.STATE_TURNING_ON->BLEState.Off
            else -> BLEState.UnknownErrorState
        }
    }


}