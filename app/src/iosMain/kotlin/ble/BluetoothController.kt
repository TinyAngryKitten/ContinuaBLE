package ble
import bledata.BLEReading
import co.touchlab.stately.collections.frozenCopyOnWriteList
import data.DeviceInfo
import iso.ServiceUUID
import platform.CoreBluetooth.*
import platform.Foundation.*
import platform.darwin.NSObject
import sample.logger


//private val hexToStr ={v : CharacteristicUUIDs -> CBUUID.UUIDWithString(v.id)}

/**
 * sThis class delegates bluetooth events to a central manager
 * that handles peripheral connections
 * and a peripheralDelegate that handles other interactions with
 * peripherals like reading characteristics.
 *
 * method declarationa and delegation is a bit funky due to
 * method delegation not working on ObjC classes, and swifts
 * heavy use of overloading with named parameters instead of
 * changing method names.
 */
class BluetoothController(private val resultCallback : (BLEReading) -> Unit)://serviesToLookFor : List<ServiceUUID>, characteristicsToLookFor : List<CharacteristicUUIDs>) :
    NSObject(),
    CBCentralManagerDelegateProtocol,
    CBPeripheralDelegateProtocol {

    val centralManager = CBCentralManager(this,null)

    //funky solution for sharing a
    var state : BLEState
        get() = stateList.first()
        set(v) {
            logger.debug(v.toString())
            stateList.set(0,v)
        }

    val stateList = frozenCopyOnWriteList<BLEState>(listOf(BLEState.UnknownErrorState))

    val peripheralController = PeripheralController(null,resultCallback)//characteristicsToLookFor.map(hexToStr))

    val discoveredDevices : MutableList<CBPeripheral> = frozenCopyOnWriteList(listOf())
    val connectedDevices : MutableList<CBPeripheral> = frozenCopyOnWriteList(listOf())

    val serviceUUIDS = listOf(
        ServiceUUID.battery,
        ServiceUUID.bloodPressure,
        ServiceUUID.glucose,
        ServiceUUID.weight,
        ServiceUUID.deviceInformation
    ).map { CBUUID.UUIDWithString(it.id) }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when(central.state) {
            CBManagerStatePoweredOff -> {
                state = BLEState.Off
                logger.debug("Bluetooth powered off")
            }
            CBManagerStateUnsupported ->{
                state = BLEState.NotSupported
                logger.debug("Bluetooth unsupported"  )
            }
            CBManagerStateResetting -> {
                state = BLEState.Resetting
                logger.debug("Bluetooth resetting")
            }
            CBManagerStateUnauthorized -> {
                state = BLEState.NotAuthorized
                logger.debug("Bluetooth unauthorized")
            }

            CBManagerStatePoweredOn -> {
                state = BLEState.On
                logger.debug("bluetooth on")
            }
            else -> {
                state = BLEState.UnknownErrorState
                logger.debug("Unknown bluetooth state")
            }//state unknown is a valid state
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
        logger.debug("ID: "+didDiscoverPeripheral.identifier.UUIDString)
        discoveredDevices.add(didDiscoverPeripheral)
        //central.connectPeripheral(didDiscoverPeripheral,null)
    }

    //on connected to peripheral
    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        logger.debug("connected to peripheral: " + didConnectPeripheral.description)
        didConnectPeripheral.delegate = this
        connectedDevices.add(didConnectPeripheral)
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
        peripheral.services?.map {
            logger.printLine(
                """
                    Service:
                ${it.toString()}
            """
            )
        }
        peripheralController.peripheral(peripheral, didDiscoverServices = didDiscoverServices)
    }
        //characteristics discovered
        override fun peripheral(
            peripheral: CBPeripheral,
            didDiscoverCharacteristicsForService: CBService,
            error: NSError?
        ) = peripheralController.peripheral(peripheral,didDiscoverCharacteristicsForService = didDiscoverCharacteristicsForService,error = error)
}