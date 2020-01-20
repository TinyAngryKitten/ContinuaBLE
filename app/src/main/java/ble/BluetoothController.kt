package ble

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.support.v4.content.ContextCompat.getSystemService
import bledata.BLEReading
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import iso.identifier
import iso.parseBLEReading
import sample.logger
import util.strRepresentation
import java.util.*
import java.util.concurrent.atomic.AtomicReference


class BluetoothController(
    val manager: BluetoothManager,
    val adapter: BluetoothAdapter,
    val context: Context
) {
    val resultCallback = AtomicReference<(BLEReading) -> Unit> {_->}
    val discoverCallback = AtomicReference<(PeripheralDescription) -> Unit> {}
    val connectCallback = AtomicReference<(PeripheralDescription) -> Unit> {}
    val stateChangedCallback = AtomicReference<(BLEState) -> Unit> {}//TODO: implement statechangedCallbacks

    private val commandQueue: Queue<Runnable> = ArrayDeque()
    private var commandQueueBusy = false
    private val bleHandler = Handler()

    val REQUEST_ENABLE_BT = 666
    private val CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    companion object {
        fun create(context: Context): BluetoothController? {
            val manager = getSystemService(context, BluetoothManager::class.java)
            val adapter = manager?.adapter//BluetoothAdapter.getDefaultAdapter()
            return if (manager == null || adapter == null) {
                null
            }
            else BluetoothController(manager, adapter, context)
        }
    }

    var discoveredDevices: MutableList<BluetoothDevice> = Collections.synchronizedList(mutableListOf<BluetoothDevice>())
    var connectedDevices: MutableList<BluetoothDevice> = Collections.synchronizedList(mutableListOf<BluetoothDevice>())

    init {
        if (manager.adapter == null) throw RuntimeException("Bluetooth not supported")
        if (!adapter.isEnabled) throw RuntimeException("Bluetooth disabled")
    }

    fun scan() {
        logger.debug("Scan starting")
        adapter.bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                logger.debug("scan finished")
                if (results != null) {
                    discoveredDevices = results.map {
                        connectToDevice(it.device.address)
                        logger.debug("discovered: ${it.device}")
                        it.device
                    }.toMutableList()

                    discoveredDevices.forEach {
                        discoverCallback.get()(
                            PeripheralDescription.fromNullable(
                                it.address,
                                it.name
                            )
                        )
                    }

                }
            }

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                if (result != null && result.device != null && !discoveredDevices.contains(result.device)) {
                    logger.debug("discovered. ${result.device.address}")
                    logger.debug("discovered. ${result.device.name}")

                    discoveredDevices.add(result.device)
                    discoverCallback.get()(
                        PeripheralDescription.fromNullable(
                            result.device.address,
                            result.device.name
                        )
                    )
                }
            }

            override fun onScanFailed(errorCode: Int) {
                logger.debug("scan error")
            }
        })
    }

    fun connectToDevice(address: String) {
        logger.debug("connecting..")
        val device = adapter.getRemoteDevice(address)
        val bleGatt = device.connectGatt(context, false, gattCallback)
        //bleGatt.discoverServices()
        logger.debug("connecting: ${device.name}")
    }

    fun disconnectFromDevice(address: String) {
        val device = adapter.getRemoteDevice(address)

    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (gatt != null && status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                logger.debug("device connected successfully: ${gatt?.device?.name} ")

                bleHandler.post {

                    connectedDevices.add(gatt.device)
                    connectCallback.get()(
                        PeripheralDescription.fromNullable(
                            gatt.device.address,
                            gatt.device.name
                        )
                    )
                    gatt.discoverServices()
                }

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            adapter.bluetoothLeScanner.flushPendingScanResults(object : ScanCallback() {})
            adapter.bluetoothLeScanner.stopScan(object : ScanCallback() {})

            bleHandler.post {
                logger.debug("services discovered on: ${gatt?.device?.name}")

                //subscribe to all relevant characteristics
                /*gatt?.services?.filter {
                service->
                //check if service is one that should be subscribed to
                ServiceUUID.getAll().find { it.equalsAndroidUUID(service.uuid) } == null
            //}?.forEach {
             */
                gatt?.services?.filter { ServiceUUID.fromNr(it.uuid.identifier) != null }
                    ?.forEach { service ->

                        logger.debug(
                            "service discovered: " + service.uuid.toString().substring(
                                4,
                                8
                            )
                        )

                        service.characteristics.forEach {

                            if (it?.properties?.and(PROPERTY_READ) ?: 0 > 0) {//if only readable, read once
                                logger.debug(
                                    "attempting to read to characteristic: ${ServiceUUID.fromNr(
                                        it.uuid.identifier
                                    )?.name ?: it.uuid.identifier}, properties. ${it.properties}"
                                )
                                readCharacteristic(it, gatt)

                            } else if (it?.properties?.and(PROPERTY_NOTIFY or PROPERTY_INDICATE) ?: 0 > 0) {//if it supports notification, subscribe
                                logger.debug(
                                    "attempting to subscribe to characteristic: ${ServiceUUID.fromNr(
                                        it.uuid.identifier
                                    )?.name ?: it.uuid.identifier}, properties. ${it.properties}"
                                )
                                setNotify(gatt, it, true)
                            }//
                        }
                    }
            }
        }

        private val CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
        fun setNotify(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            enable: Boolean
        ): Boolean {

            // Get the CCC Descriptor for the characteristic
            val descriptor = characteristic.getDescriptor(UUID.fromString(CCC_DESCRIPTOR_UUID));
            if (descriptor == null) {
                logger.error("Could not get CCC descriptor for characteristic ${characteristic.uuid}")
                return false
            }

            // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
            val properties = characteristic.properties
            val desciptorValue = if ((properties and PROPERTY_NOTIFY) > 0) {
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            } else if ((properties and PROPERTY_INDICATE) > 0) {
                BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            } else if (!enable) {
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            } else {
                logger.error("Characteristic ${characteristic.getUuid()} does not support indicate or notify")
                return false
            }

            val result = commandQueue.add(Runnable {
                // First set notification for Gatt object
                if (!gatt.setCharacteristicNotification(descriptor.characteristic, enable)) {
                    logger.error("setCharacteristicNotification failed for descriptor: ${descriptor.uuid}")
                    completedCommand()
                } else {

                descriptor.setValue(desciptorValue);
                val result = gatt.writeDescriptor(descriptor);
                if (!result) {
                    logger.error("unable to write to descriptor of characteristic: ${characteristic.uuid}")
                    completedCommand()
                }//if it didnt fail complete command will be called in onWriteDescriptor
            }})

            if (result) {
                nextCommand()
            } else {
                logger.error("unable to queue notify command")
            }

            return result
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            // Do some checks first
            val parentCharacteristic = descriptor.characteristic
            if (status != GATT_SUCCESS) {
                logger.error("failed to write to descriptor on device ${gatt.device?.name} on characteristic ${parentCharacteristic.uuid}")
            }
            completedCommand()
        }

        fun readCharacteristic(
            characteristic: BluetoothGattCharacteristic,
            gatt: BluetoothGatt
        ): Boolean {
            val result = commandQueue.add(Runnable {
                if (gatt.readCharacteristic(characteristic)) {
                    logger.debug("characteristic read: ${characteristic.uuid}")
                } else {
                    logger.error("unable to read characteristic ${characteristic.uuid}")
                    completedCommand()
                }
            })
            //attempt to run command
            nextCommand()
            return result
        }

        private fun nextCommand() {
            // stop if queue is still bussy
            if (commandQueueBusy) {
                return
            }
            // Execute the next command in the queue
            if (commandQueue.size > 0) {
                val bluetoothCommand = commandQueue.peek()
                commandQueueBusy = true
                bleHandler.post {
                    try {
                        bluetoothCommand.run()
                    } catch (ex: Exception) {

                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (BluetoothGatt.GATT_SUCCESS == status) {
                if (gatt == null || characteristic == null) return logger.error("unable to read characteristic, gatt or characteristic is null")
                logger.debug("read received: from ${gatt.device.name}, characteristic: ${characteristic.uuid}, values: ${characteristic.value.strRepresentation()}")

                bleHandler.post {
                    val result =
                        packageBleReading(
                            characteristic.value,
                            gatt.device,
                            characteristic
                        )
                    resultCallback.get()(result)
                }
            } else logger.error("GATT ERROR STATUS")
            completedCommand()
        }

        private fun completedCommand() {
            commandQueueBusy = false
            commandQueue.poll()
            nextCommand()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {

            if(gatt == null || characteristic == null) return logger.error("gatt(${gatt?.device}) or characteristic(${characteristic?.uuid}) is null on changed events")
            logger.debug("characteristic change received ${characteristic.value.strRepresentation()}")
            bleHandler.post {
                val result =
                    packageBleReading(
                        characteristic.value,
                        gatt.device,
                        characteristic
                    )

                resultCallback.get()(result)
            }
        }
    }
}