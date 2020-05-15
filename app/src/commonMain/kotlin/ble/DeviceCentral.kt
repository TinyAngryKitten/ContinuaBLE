package ble

import bledata.BLEReading
import bledata.BLEState
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.DataRecord
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import iso.parseBLEReading
import util.logger
import kotlin.native.concurrent.SharedImmutable

class DeviceCentral(val bleCentral : BleCentralInterface){
    @SharedImmutable
    val onRecordReceived = AtomicReference({it : DataRecord-> logger.debug(it.toString())}.freeze())
    val onDeviceDiscovered = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onDeviceConnected = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onStateChanged = AtomicReference({_: BLEState -> }.freeze())
    //val onCharacteristicDiscovered = AtomicReference({_:PeripheralDescription,_: CharacteristicUUIDs, _:ServiceUUID-> }.freeze())
    //val onDeviceCapabilitiesDiscovered = AtomicReference({record: DataRecord->recordCentral.addDeviceCapabilities }.freeze())


    //val deviceCapabilities : Map<String, DeviceCapabilities.DeviceServices> = frozenHashMap()

    private val recordCentral = IntermediateRecordStorage {
            record: DataRecord ->
        logger.info("Record received:")
        logger.info("$record")
        onRecordReceived.get()(record).freeze()
    }

    //subscribe to changes in bluetooth connection
    init {
        bleCentral.changeResultCallback({
            reading : BLEReading->
            logger.debug("resultCallback: $reading")
            recordCentral.addRecord(
                parseBLEReading(
                    reading
                )
            )
        }.freeze())

        bleCentral.changeOnConnectCallback({device : PeripheralDescription ->
            logger.info("connected to: ${device.name}")
            onDeviceConnected.get()(device)
        }.freeze())
        bleCentral.changeOnDiscoverCallback({device : PeripheralDescription ->
            logger.info("discovered: ${device.name} (${device.UUID})")
            onDeviceDiscovered.get()(device)
        }.freeze())
        bleCentral.changeStateChangeCallback({state : BLEState ->
            logger.info("Bluetooth state changed: $state")
            onStateChanged.get()(state)
        }.freeze())
        bleCentral.changeOnCharacteristicDiscovered({
            device: PeripheralDescription, characteristicUUID: CharacteristicUUIDs, serviceUUID : ServiceUUID->
            recordCentral.addDeviceCapability(device,characteristicUUID,serviceUUID)
            logger.info("Characteristic discovered: $characteristicUUID")
        }.freeze())
    }

    fun connectToDevice(device: PeripheralDescription) = bleCentral.connectToDevice(device)
    fun scanForDevices() = bleCentral.scanForDevices()

    val state get() = bleCentral.bleState()
}