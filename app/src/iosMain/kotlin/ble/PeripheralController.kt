package ble

import bledata.BLEReading
import platform.CoreBluetooth.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import sample.Logger
import sample.logger


class PeripheralController(
    val characteristicUUIDs : List<CBUUID>,
    val readingReceived : (BLEReading) -> Unit = {v-> logger.debug(v.toString())}
): NSObject(), CBPeripheralDelegateProtocol {

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        peripheral.services?.let { services ->//type information are erased from servies due to the kotlin-swift-obj bridge
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
                    if(char.isNotifying) peripheral.setNotifyValue(true,char)
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
    ) = readingReceived(
            packageBleReading(
                didUpdateValueForCharacteristic.value,
                peripheral,
                didUpdateValueForCharacteristic
            )
        )
}