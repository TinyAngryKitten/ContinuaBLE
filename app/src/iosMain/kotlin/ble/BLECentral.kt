package ble

import bledata.BLEReading
import bledata.BLEState
import data.PeripheralDescription
import iso.ServiceUUID
import platform.CoreBluetooth.CBUUID
import sample.logger

actual class BLECentral {
    val controller = BluetoothController()

    actual fun bleState() = controller.state;

    actual fun scanForDevices() {
        if(bleState() != BLEState.On) {
            logger.error("BLE not on, stopping scan")
            return
        }else if(controller.centralManager.isScanning) {
            logger.debug("stopping old scan before starting new")
            controller.centralManager.stopScan()
            controller.discoveredDevices.removeAll(controller.discoveredDevices)
        }

        controller.centralManager.scanForPeripheralsWithServices(
            ServiceUUID.getAll().map { CBUUID.UUIDWithString(it.id) },
            null)

        logger.debug("scan started")
    }

    actual fun connectToDevice(deviceDescription: PeripheralDescription) {
        controller.discoveredDevices.find { it.identifier.UUIDString == deviceDescription.UUID }?.let {
            if(controller.centralManager.isScanning) controller.centralManager.stopScan()
            logger.debug("Connecting to peripheral: $it")
            controller.centralManager.connectPeripheral(it,null)
        } ?: logger.error("Attempted to connect to a peripheral that does not exist or are not in range: $deviceDescription")
    }

    actual fun changeOnDiscoverCallback(callback: (PeripheralDescription)->Unit) {
        controller.discoverCallback.compareAndSet(controller.discoverCallback.value,callback)
    }

    actual fun changeOnConnectCallback(callback : (PeripheralDescription)-> Unit) {
        controller.connectCallback.compareAndSet(controller.connectCallback.value,callback)
    }

    actual fun changeResultCallback(callback: (BLEReading) -> Unit) {
        controller.peripheralController.readingReceivedCallback.compareAndSet(controller.peripheralController.readingReceivedCallback.value,callback)
    }

    actual fun changeStateChangeCallback(callback: (BLEState) -> Unit) {
        controller.stateChangedCallback.compareAndSet(controller.stateChangedCallback.value,callback)
    }

}