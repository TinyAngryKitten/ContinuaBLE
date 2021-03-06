package ble

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.support.v4.content.ContextCompat.getSystemService
import bledata.*
import bledata.PeripheralDescription
import co.touchlab.stately.concurrency.value
import gatt.*
import util.logger
import util.strRepresentation
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class BluetoothController(
    val manager: BluetoothManager,
    val adapter: BluetoothAdapter,
    val context: Context
) : BleCentralCallbackInterface {
    val resultCallback = AtomicReference<(BLEReading) -> Unit> {_->}
    val discoverCallback = AtomicReference<(PeripheralDescription) -> Unit> {}
    val connectCallback = AtomicReference<(PeripheralDescription) -> Unit> {}
    val stateChangedCallback = AtomicReference<(BLEState) -> Unit> {TODO("Android does not have a way of notifying the application of changes in state")}
    val characteristicDiscoveredCallback = AtomicReference<(PeripheralDescription, CharacteristicUUIDs, ServiceUUID) -> Unit> { _, _, _->}

    private val queue: Queue<Runnable> = ArrayDeque()
    private var queueBusy = false
    private val bleHandler = Handler()

    private val descriptorUUID = "00002902-0000-1000-8000-00805f9b34fb"

    companion object {
        fun create(context: Context): BluetoothController? {
            val manager = getSystemService(context, BluetoothManager::class.java)
            val adapter = manager?.adapter
            return if (manager == null || adapter == null) {
                error("manager = $manager and adapter = $adapter")
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

    fun stopScan() {
        adapter.bluetoothLeScanner.stopScan(scanCallback)
        logger.info("Scan ended")
    }

    fun scan() {
        logger.debug("Scan starting")

        bleHandler.postDelayed({
            adapter.bluetoothLeScanner.stopScan(scanCallback)
            logger.info("Scan ended")
        }, 60000)

        adapter.bluetoothLeScanner.startScan(
            ServiceUUID.getDiscoverable().map {
                ScanFilter
                    .Builder()
                    .setServiceUuid(
                        ParcelUuid(UUID.fromString(it.toAndroidUUID()))
                    )
                    .build()
            },
            ScanSettings.Builder().build(),
            scanCallback
        )
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
            if (
                result != null &&
                result.device != null &&
                !discoveredDevices.contains(result.device)) {
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
            logger.error("scan failed")
        }
    }

    fun connectToDevice(address: String) {
        logger.info("connecting...")
        val device = adapter.bondedDevices.find { it.address == address } ?: adapter.getRemoteDevice(address)
        if(device == null) {
            logger.info("Device $address has not been discovered yet")
        } else {
            device.connectGatt(context, true, gattCallback)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (gatt != null && status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
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

        /**
         * Starts by updating the time on sensor,
         * delays for a few seconds for the update to take effect,
         * then subscribes to new measurements
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            bleHandler.post {
                logger.debug("services discovered on: ${gatt?.device?.name}")

                gatt?.services?.filter {
                    ServiceUUID.fromNr(it.uuid.identifier) != null
                }?.map {
                    it.characteristics.filter {
                        CharacteristicUUIDs.dateTime.equalsAndroidUUID(it.uuid) ||
                                CharacteristicUUIDs.currentTime.equalsAndroidUUID(it.uuid)
                    }
                }?.flatMap { it.filterNotNull() }?.forEach {
                    logger.debug("Writing time to device")

                    val isCurrentTime = CharacteristicUUIDs.currentTime.equalsAndroidUUID(it.uuid)
                    val calendar = Calendar.getInstance()
                    val dateTime = GATTValue.DateTime(
                        GATTValue.Year.fromInt(calendar.get(Calendar.YEAR)),
                        GATTValue.Month.fromInt(calendar.get(Calendar.MONTH) + 1),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND)
                    )

                    it.value = if (isCurrentTime)
                        CurrentTime(
                            ExactTime(
                                DayDateTime(
                                    dateTime, getDayOfWeek()
                                ),
                                GATTValue.UInt8(0)
                            ),
                            AdjustReason(externalReferenceTimeUpdate = true)
                        ).toByteArray()
                    else dateTime.toByteArray()

                    logger.info("Time updated")
                    logger.debug("Time bytes:")
                    dateTime.toByteArray().forEach {
                        logger.debug(it.strRepresentation())
                    }

                    writeCharacteristic(it, gatt)
                }

                val peripheralDescription = PeripheralDescription(gatt?.device?.address ?: "")

                //delay subsribing to measurements for a few seconds
                //to time update takes effect
                Handler().postDelayed( {
                    gatt?.services?.filter { ServiceUUID.fromNr(it.uuid.identifier) != null }
                        ?.forEach { service ->

                            service.characteristics.forEach {
                                characteristicDiscoveredCallback.get()(
                                    peripheralDescription,
                                    CharacteristicUUIDs.fromNr(it.uuid.identifier),
                                    ServiceUUID.fromNr(service.uuid.identifier) ?: ServiceUUID.unknown
                                )

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
                                    subscribeToCharacteristic(gatt, it, true)
                                }//
                            }
                        }
                }, 3000)
            }
        }

        fun subscribeToCharacteristic(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            enable: Boolean
        ): Boolean {

            val descriptor = characteristic.getDescriptor(UUID.fromString(descriptorUUID))

            if (descriptor == null) {
                logger.error("Cant get descriptor for characteristic: ${characteristic.uuid}")
                return false
            }

            val props = characteristic.properties
            val desciptorValue = when {
                props and PROPERTY_NOTIFY > 0 -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                props and PROPERTY_INDICATE > 0 -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                !enable -> BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                else -> {
                    logger.error("Cannot subscribe to ${characteristic.uuid}")
                    return false
                }
            }

            val result = queue.add( Runnable {
                if (!gatt.setCharacteristicNotification(descriptor.characteristic, enable)) {
                    logger.error("cant set notify for: ${descriptor.uuid}")
                    completedBTAction()
                } else {
                    descriptor.value = desciptorValue
                    val result = gatt.writeDescriptor(descriptor)

                    if (!result) {
                        logger.error("cant write to descriptor for characteristic: ${characteristic.uuid}")
                        completedBTAction()
                    } else gatt.readCharacteristic(characteristic)
            }})

            if (result) nextBTAction()
            else logger.error("unable to queue subscribtion")

            return result
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            logger.debug("descriptor written: ${descriptor.uuid.identifier}")
            val characteristic = descriptor.characteristic
            if (status != GATT_SUCCESS) logger.error("could not write descriptor on ${gatt.device?.name}, characteristic ${characteristic.uuid}")
            completedBTAction()
        }

        fun writeCharacteristic(
            characteristic: BluetoothGattCharacteristic,
            gatt: BluetoothGatt
        ): Boolean {
            logger.debug("writing characteristic: ${characteristic.uuid.identifier}")
            val result = queue.add(Runnable {
                if (gatt.writeCharacteristic(characteristic)) {
                    logger.debug("Write success: ${characteristic.uuid}")
                } else {
                    logger.error("Write failed: ${characteristic.uuid}")
                    completedBTAction()
                }
            })
            //attempt to run command
            nextBTAction()
            return result
        }

        fun readCharacteristic(
            characteristic: BluetoothGattCharacteristic,
            gatt: BluetoothGatt
        ): Boolean {
            logger.debug("reading characteristic: ${characteristic.uuid.identifier}")
            val result = queue.add(Runnable {
                if (gatt.readCharacteristic(characteristic)) {
                    logger.debug("characteristic read: ${characteristic.uuid}")
                } else {
                    logger.error("unable to read characteristic ${characteristic.uuid}")
                    completedBTAction()
                }
            })
            nextBTAction()
            return result
        }

        private fun nextBTAction() {
            if (queueBusy) return
            if (queue.isNotEmpty()) {

                val next = queue.peek()
                queueBusy = true
                bleHandler.post {
                    try {
                        next.run()
                    } catch (ex: Exception) {}
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            logger.debug("characteristic read: ${characteristic?.uuid?.identifier}")
            if (GATT_SUCCESS == status) {
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
            } else logger.error("GATT err: $status characteristic: ${characteristic?.uuid?.identifier}")
            completedBTAction()
        }

        private fun completedBTAction() {
            queueBusy = false
            queue.poll()
            nextBTAction()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            logger.debug("char written: ${characteristic?.uuid?.identifier}")
            logger.debug("write status: $status")
            completedBTAction()
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

    override fun changeStateChangeCallback(callback: (BLEState) -> Unit) { stateChangedCallback.compareAndSet(stateChangedCallback.value, callback) }
    override fun changeResultCallback(callback: (BLEReading) -> Unit) { resultCallback.compareAndSet(resultCallback.value, callback)}
    override fun changeOnDiscoverCallback(callback: (PeripheralDescription) -> Unit) { discoverCallback.compareAndSet(discoverCallback.value, callback) }
    override fun changeOnConnectCallback(callback: (PeripheralDescription) -> Unit) { connectCallback.compareAndSet(connectCallback.value, callback) }
    override fun changeOnCharacteristicDiscovered(callback: (PeripheralDescription, CharacteristicUUIDs, ServiceUUID) -> Unit) { characteristicDiscoveredCallback.compareAndSet(characteristicDiscoveredCallback.value, callback) }
}