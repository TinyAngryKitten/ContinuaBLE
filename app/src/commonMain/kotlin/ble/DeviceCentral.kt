package ble

import bledata.BLEReading
import bledata.BLEState
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.DataRecord
import data.PeripheralDescription
import iso.parseBLEReading
import sample.logger
import kotlin.native.concurrent.SharedImmutable

class DeviceCentral(val bleCentral : BleCentralInterface){
    @SharedImmutable
    val onRecordReceived = AtomicReference({it : DataRecord-> logger.debug(it.toString())}.freeze())
    val onDeviceDiscovered = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onDeviceConnected = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onStateChanged = AtomicReference({_: BLEState -> }.freeze())
    val onCharacteristicDiscovered = AtomicReference({_: BLEState -> }.freeze())
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
    }

    fun connectToDevice(device: PeripheralDescription) = bleCentral.connectToDevice(device)
    fun scanForDevices() = bleCentral.scanForDevices()

    val state get() = bleCentral.bleState()
}