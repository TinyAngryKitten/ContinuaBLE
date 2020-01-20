package ble

import bledata.BLEReading
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.DataRecord
import data.PeripheralDescription
import iso.parseBLEReading
import sample.logger
import kotlin.native.concurrent.SharedImmutable

class DeviceCentral(private val bleCentral : BLECentral){
    @SharedImmutable
    val onRecordReceived = AtomicReference({it : DataRecord-> logger.debug(it.toString())}.freeze())
    val onDeviceDiscovered = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onDeviceConnected = AtomicReference({_ : PeripheralDescription -> }.freeze())
    val onStateChanged = AtomicReference({_:BLEState -> }.freeze())

    private val recordCentral = RecordCentral({
            record: DataRecord ->
        logger.debug("Record received: $record")
        onRecordReceived.get()(record).freeze()
    })

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
            logger.debug("onConnectCallback: ${device.name}")
            onDeviceConnected.get()(device)
        }.freeze())
        bleCentral.changeOnDiscoverCallback({device : PeripheralDescription ->
            logger.debug("onDiscoverCallback: ${device.name}")
            onDeviceDiscovered.get()(device)
        }.freeze())
        bleCentral.changeStateChangeCallback({state : BLEState ->
            logger.debug("state changed: $state")
            onStateChanged.get()(state)
        }.freeze())
    }

    fun connectToDevice(device: PeripheralDescription) = bleCentral.connectToDevice(device)
    fun scanForDevices() = bleCentral.scanForDevices()

    val state get() = bleCentral.bleState()
}