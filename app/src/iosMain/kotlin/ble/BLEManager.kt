package ble

import data.DeviceInfo
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import iso.parseBLEReading
import platform.CoreBluetooth.CBUUID
import sample.logger

actual object BLEManager {
    val controller = BluetoothController{
        logger.debug("received reading: $it")
        parseBLEReading(it)
    }

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
            logger.debug("Connecting to peripheral: $it")
            controller.centralManager.connectPeripheral(it,null)
        } ?: logger.error("Attempted to connect to a peripheral that does not exist or are not in range: $deviceDescription")
    }

}