package ble

import bledata.BLEReading
import platform.CoreBluetooth.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import sample.logger
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze


class PeripheralController(
    val characteristicUUIDs : List<CBUUID>?,
    readingReceived : (BLEReading) -> Unit = {v:BLEReading-> logger.debug(v.toString()) }.freeze()
): NSObject(), CBPeripheralDelegateProtocol {
    val readingReceivedCallback = AtomicReference(readingReceived.freeze())

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        peripheral.services?.let { services ->//type information are erased from servies due to the kotlin-swift-obj bridge
            logger.printLine(services.joinToString(", ") )
            services.map { logger.printLine("UUID: " + (it as CBService).UUID) }
            services.map { (it as CBService).includedServices?.map { logger.printLine("Nested service:"+ (it as CBService).UUID) } }
            services.map{peripheral.discoverCharacteristics(characteristicUUIDs,it as CBService)}
        }
    }

    //characteristic discovered
    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverCharacteristicsForService: CBService,
        error: NSError?
    ) {
        didDiscoverCharacteristicsForService.characteristics?.let {characteristics->
            characteristics.map {
                if(it != null) {
                    val char = it as CBCharacteristic
                    if(char.isNotifying) {
                        peripheral.setNotifyValue(true,char)
                        peripheral.readValueForCharacteristic(char)
                    }
                    else peripheral.readValueForCharacteristic(it)
                }
            }
        }
    }

    //Characteristic updateds
    override fun peripheral(
        peripheral: CBPeripheral,
        didUpdateValueForCharacteristic: CBCharacteristic,
        error: NSError?
    ) = readingReceivedCallback.value(
            packageBleReading(
                didUpdateValueForCharacteristic.value,
                peripheral,
                didUpdateValueForCharacteristic
            ).freeze()
        )
}