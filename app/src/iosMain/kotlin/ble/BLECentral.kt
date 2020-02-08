package ble

import bledata.BLEReading
import bledata.BLEState
import co.touchlab.stately.freeze
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import kotlinx.cinterop.*
import kotlinx.cinterop.nativeHeap.alloc
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBCharacteristicWriteWithResponse
import platform.CoreBluetooth.CBService
import platform.CoreBluetooth.CBUUID
import platform.Foundation.NSData
import platform.Foundation.create
import platform.darwin.UInt8
import platform.posix.int32_t
import platform.posix.int32_tVar
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

    fun writeCharacteristic(deviceDescription: PeripheralDescription) {
        controller.connectedDevices.find { it.identifier.UUIDString == deviceDescription.UUID }?.let {
            peripheral->
            if(controller.centralManager.isScanning) controller.centralManager.stopScan()
            val service = peripheral.services?.find { (it as CBService).UUID.UUIDString == ServiceUUID.glucose.id } as CBService?
            val characteristic = service?.characteristics?.find { (it as CBCharacteristic).UUID.UUIDString == CharacteristicUUIDs.glucoseControlPoint.id } as CBCharacteristic?
                ?: return

            val bytedata = 1
            val data = logger.nsdata.value ?: return
            logger.info("write attempted...")

            peripheral.writeValue(
                data as NSData,
                characteristic,
                CBCharacteristicWriteWithResponse
                )
            //controller.centralManager
        } ?: logger.error("Attempted to connect to a peripheral that does not exist or are not in range: $deviceDescription")
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
        controller.connectCallback.compareAndSet(controller.connectCallback.value,{
            it:PeripheralDescription->
            writeCharacteristic(it)
            callback(it)
        }.freeze())
    }

    actual fun changeResultCallback(callback: (BLEReading) -> Unit) {
        controller.peripheralController.readingReceivedCallback.compareAndSet(controller.peripheralController.readingReceivedCallback.value,callback)
    }

    actual fun changeStateChangeCallback(callback: (BLEState) -> Unit) {
        controller.stateChangedCallback.compareAndSet(controller.stateChangedCallback.value,callback)
    }

}