package ble
import iso.CharacteristicUUIDs
import iso.ServiceUUID
import iso.parseBLEReading
import platform.CoreBluetooth.*
import platform.Foundation.*
import platform.darwin.NSObject
import sample.logger


private val hexToStr ={v : CharacteristicUUIDs -> CBUUID.UUIDWithString(v.id)}

/**
    * This class delegates bluetooth events to a central manager
 * that handles peripheral connections
 * and a peripheralDelegate that handles other interactions with
 * peripherals like reading characteristics.
 *
 * method declarationa and delegation is a bit funky due to
 * method delegation not working on ObjC classes, and swifts
 * heavy use of overloading with named parameters instead of
 * changing method names.
 */
class BluetoothController(serviesToLookFor : List<ServiceUUID>, characteristicsToLookFor : List<CharacteristicUUIDs>) :
    NSObject(),
    CBCentralManagerDelegateProtocol,
    CBPeripheralDelegateProtocol {

    val centralManager = CBCentralManager(this,null)

    val peripheralController = PeripheralController(characteristicsToLookFor.map(hexToStr)) {
        logger.debug("raw bluetooth reading received: \n"+it.toString())
        parseBLEReading(it)
    }

    val serviceUUIDS = serviesToLookFor
        .map { CBUUID.UUIDWithString(it.id) }

    var discoveredDevices = listOf<CBPeripheral>()
    var connectedDevices = listOf<CBPeripheral>()

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when(central.state) {
            CBManagerStatePoweredOff -> logger.debug("powered off")
            CBManagerStateUnsupported -> logger.debug("unsupported"  )
            CBManagerStateResetting -> logger.debug("resetting")
            CBManagerStateUnauthorized -> logger.debug("unauthorized")

            CBManagerStatePoweredOn -> {
                logger.debug("powered on")

                centralManager.scanForPeripheralsWithServices(
                    serviceUUIDS,null
                )
            }
            else -> logger.debug("unknown bluetooth state")//state unknown is a valid state
        }
    }

    //on discover new peripheral
    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {
        logger.debug("discovered: "+didDiscoverPeripheral.name)
        central.connectPeripheral(didDiscoverPeripheral,null)

    }

    //on connected to peripheral
    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        logger.debug("connected to peripheral: "+ didConnectPeripheral.description)
        didConnectPeripheral.delegate = this
        didConnectPeripheral.discoverServices(null)
    }

    override fun centralManager(central: CBCentralManager, didFailToConnectPeripheral: CBPeripheral, error: NSError?) {
        logger.debug("failed to connect to peripheral"+ didFailToConnectPeripheral.description)
    }

    //PERIPHERAL METHODS

    //Characteristic updated
    override fun peripheral(
        peripheral: CBPeripheral,
        didUpdateValueForCharacteristic: CBCharacteristic,
        error: NSError?
    ) = peripheralController.peripheral(peripheral,didUpdateValueForCharacteristic = didUpdateValueForCharacteristic,error = error)

    //discovered services
    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverServices: NSError?
    ) {
        peripheral.services?.map { logger.printLine(it.toString()) }
        peripheralController.peripheral(peripheral, didDiscoverServices = didDiscoverServices)
    }
        //characteristics discovered
        override fun peripheral(
            peripheral: CBPeripheral,
            didDiscoverCharacteristicsForService: CBService,
            error: NSError?
        ) = peripheralController.peripheral(peripheral,didDiscoverCharacteristicsForService = didDiscoverCharacteristicsForService,error = error)
}