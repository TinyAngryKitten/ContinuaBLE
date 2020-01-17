package ble

import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.concurrency.AtomicReference
import data.DataRecord
import data.PeripheralDescription
import iso.parseBLEReading

class DeviceCentral(private val bleCentral : BLECentral){
    val onRecordReceived = AtomicReference {_ : DataRecord-> }
    val onDeviceDiscovered = AtomicReference {_ : PeripheralDescription -> }
    val onDeviceConnected = AtomicReference {_ : PeripheralDescription -> }
    val onStateChanged = AtomicReference{_:BLEState -> }

    private val recordCentral = RecordCentral(onRecordReceived.get())

    //subscribe to changes in bluetooth connection
    init {
        bleCentral.changeResultCallback {
            recordCentral.addRecord(
                parseBLEReading(
                    it
                )
            )
        }

        bleCentral.changeOnConnectCallback{onDeviceConnected.get()(it)}
        bleCentral.changeOnDiscoverCallback{onDeviceDiscovered.get()(it)}
        bleCentral.changeStateChangeCallback { onStateChanged.get()(it) }
    }

    fun connectToDevice(device: PeripheralDescription) = bleCentral.connectToDevice(device)
    fun scanForDevices() = bleCentral.scanForDevices()

    val state get() = bleCentral.bleState()

}