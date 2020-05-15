package ble

import bledata.BLEReading
import platform.CoreBluetooth.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import util.logger
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze


class PeripheralController(
    val characteristicUUIDs : List<CBUUID>?,
    readingReceived : (BLEReading) -> Unit = {v:BLEReading-> logger.debug(v.toString()) }.freeze()
): NSObject(), CBPeripheralDelegateProtocol {
    val readingReceivedCallback = AtomicReference(readingReceived.freeze())

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        if(didDiscoverServices != null) return logger.info("error occured on discover services): ${didDiscoverServices.description}")
        peripheral.services?.let { services ->//type information are erased from servies due to the kotlin-swift-obj bridge
            logger.printLine(services.joinToString(", ") )
            services.map { logger.debug("UUID: " + (it as CBService).UUID) }
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
        if(error != null) return logger.debug("error occured on discover characteristics(${didDiscoverCharacteristicsForService.description}) Description: ${error?.description}, code: ${error?.code}")
        else didDiscoverCharacteristicsForService.characteristics?.let {characteristics->

            characteristics.map {
                if(it != null) {
                    val char = it as CBCharacteristic
                    if(char.isNotifying) {
                        peripheral.setNotifyValue(true,char)
                        peripheral.discoverDescriptorsForCharacteristic(char)
                        logger.info("set notify for char with prop: ${char.properties} (${char.UUID})")
                        logger.info("current charval: ${char.value}")
                        peripheral.readValueForCharacteristic(char)
                    }
                    else peripheral.readValueForCharacteristic(it)
                }
            }
        }
    }



    override fun peripheral(
        peripheral: CBPeripheral,
        didWriteValueForDescriptor: CBDescriptor,
        error: NSError?
    ) {
    }

    //Characteristic updateds
    override fun peripheral(
        peripheral: CBPeripheral,
        didUpdateValueForCharacteristic: CBCharacteristic,
        error: NSError?
    ) {
        if(error != null) {
            logger.debug("error occured when reading characteristic(${didUpdateValueForCharacteristic.UUID.UUIDString}) update: ${error.description} from device: ${peripheral.description}")
        } else readingReceivedCallback.value(
            packageBleReading(
                didUpdateValueForCharacteristic.value,
                peripheral,
                didUpdateValueForCharacteristic
            ).freeze()
        )
    }
}