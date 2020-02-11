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
import bledata.BLEState
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import iso.identifier
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

    private val CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    companion object {
        fun create(context: Context): BluetoothController? {
            val manager = getSystemService(context, BluetoothManager::class.java)
            val adapter = manager?.adapter//BluetoothAdapter.getDefaultAdapter()
            return if (manager == null || adapter == null) {
                error("manager = $manager and adapter = $adapter")
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

        bleHandler.postDelayed({
            adapter.bluetoothLeScanner.stopScan(scanCallback)
        }, 60000)//stop scan after 1 minute

        adapter.bluetoothLeScanner.startScan(scanCallback)
        /*
        logger.debug("IS DISCOVERING: "+adapter.isDiscovering)
        val device = adapter.bondedDevices.find { it.address == "34:03:DE:0D:51:16" }
        logger.debug("DEVICE: $device")
        //val device = adapter.getRemoteDevice(address)
        val bleGatt = device?.connectGatt(context, false, gattCallback)
        //bleGatt.discoverServices()
        logger.debug("connecting: ${device?.name}")*/
    }

    val scanCallback = object : ScanCallback() {
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
    }

    fun connectToDevice(address: String) {
        logger.debug("connecting..")
        val device = adapter.bondedDevices.find { it.address == address } ?: adapter.getRemoteDevice(address)

        //val device = adapter.getRemoteDevice(address)
        val bleGatt = device?.connectGatt(context, true, gattCallback)
        //bleGatt.discoverServices()
        logger.debug("connecting: ${device?.name}")
    }

    fun disconnectFromDevice(address: String) {
        val device = adapter.getRemoteDevice(address)

    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (gatt != null && status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                logger.debug("device connected successfully: ${gatt.device?.name} ")

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

            bleHandler.post {
                logger.debug("services discovered on: ${gatt?.device?.name}")

                //subscribe to all relevant characteristics
                /*gatt?.services?.filter {
                service->
                //check if service is one that should be subscribed to
                ServiceUUID.getAll().find { it.equalsAndroidUUID(service.uuid) } == null
            //}?.forEach {
             */

                gatt?.services//?.filter { ServiceUUID.fromNr(it.uuid.identifier) != null }
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

        private val descriptorUUID = "00002902-0000-1000-8000-00805f9b34fb"
        fun setNotify(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            enable: Boolean
        ): Boolean {
            logger.debug("setting notify")

            // Get the CCC Descriptor for the characteristic
            val descriptor = characteristic.getDescriptor(UUID.fromString(descriptorUUID));
            if (descriptor == null) {
                logger.error("Could not get CCC for: ${characteristic.uuid}")
                return false
            }

            // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
            val props = characteristic.properties
            val desciptorValue = if ((props and PROPERTY_NOTIFY) > 0) {
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            } else if ((props and PROPERTY_INDICATE) > 0) {
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
                else gatt.readCharacteristic(characteristic)
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
            logger.debug("descriptor written: ${descriptor.uuid.identifier}")
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
            logger.debug("reading characteristic: ${characteristic.uuid.identifier}")
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
            logger.debug("characteristic read: ${characteristic?.uuid?.identifier}")
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
            } else logger.error("GATT ERROR STATUS: $status (${characteristic?.uuid?.identifier})")
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
            logger.debug("characteristic changed: ${characteristic?.uuid?.identifier}")
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