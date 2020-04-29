import UIKit
import app
import CoreBluetooth
import RxBluetoothKit
import RxSwift
//glucose: FC637664-D732-5B33-193A-8DF96288E3A6
//bp: 552D7A89-7BB7-C25D-6936-5AF9C752CC03
class TimeService: ServiceIdentifier {
    var uuid = CBUUID(string: ServiceUUID.currentTime().nr)
}
class TimeCharacteristic: CharacteristicIdentifier {
    var uuid: CBUUID = CBUUID(string: CharacteristicUUIDs.currentTime().nr)
    //Service to which characteristic belongs
    var service: ServiceIdentifier = TimeService()
}

enum DeviceService: String, ServiceIdentifier {
    case monitoring = "1b0d1200-a720-f7e9-46b6-31b601c4fca1"

    var uuid: CBUUID {
        return CBUUID(string: self.rawValue)
    }
}

class RxTest : BleCentralInterface{
    
    var onConnect : (PeripheralDescription) -> Void = {_ in print("connect callback not changed")}
    var onDiscover : (PeripheralDescription) -> Void = {_ in print("onDiscover callback not changed")}
    var onResult : (BLEReading) -> Void = {_ in print("on result callback not changed")}
    var stateChanged : (BLEState) -> Void = {_ in print("state changed callback not changed")}
    var onCharacteristicDiscovered: (PeripheralDescription,CharacteristicUUIDs,ServiceUUID) -> Void = {_,_,_ in print("discovered characteristic callback not changed")}
    
    var manager : CentralManager!
    let services = ServiceUUID.Companion().getAll().map({c in CBUUID(string: c.nr)})
    let characteristics = CharacteristicUUIDs.Companion().getAll().map({c in CBUUID(string: c.nr)})
    let timeCharacteristics = [
        CharacteristicUUIDs.dateTime(),
        CharacteristicUUIDs.currentTime()
    ].map({c in CBUUID(string: c.nr)})
    
    var discoveredDevices : [Peripheral] = []
    let messageParser = BleMessageParser()
    
    var currentDiscovery : Disposable? = nil
    var stateDisposable : Disposable? = nil
    
    init() {
        manager = CentralManager(queue: .main)
        //subscribe to bluetooth state
        stateDisposable =  manager.observeState()
            .startWith(manager.state)
            .subscribe({
                state in
                self.stateChanged(
                    self.managerStateToBLEState(state: state.element ?? BluetoothState.unknown)
                )
            })
    }
    
    func managerStateToBLEState(state: BluetoothState) -> BLEState {
       if(state == .poweredOff) {return BLEState.Off()}
       else if(state == .poweredOn){return BLEState.On()}
       else if(state == .unauthorized){return BLEState.NotAuthorized()}
       else if(state == .unsupported){return BLEState.NotSupported()}
       else if(state == .resetting){return BLEState.Resetting()}
       else {return BLEState.UnknownErrorState()}
   }
    func bleState() -> BLEState {
        return managerStateToBLEState(state: manager.state)
    }
    
    func changeOnConnectCallback(callback: @escaping (PeripheralDescription) -> Void) {
        onConnect = callback
    }
    
    func changeOnDiscoverCallback(callback: @escaping (PeripheralDescription) -> Void) {
        onDiscover = callback
    }
    
    func changeResultCallback(callback: @escaping (BLEReading) -> Void) {
        onResult = callback
    }
    
    func changeStateChangeCallback(callback: @escaping (BLEState) -> Void) {
        stateChanged = callback
    }
    
    func changeOnCharacteristicDiscovered(callback: @escaping (PeripheralDescription,CharacteristicUUIDs,ServiceUUID) -> Void) {
        onCharacteristicDiscovered = callback
    }
    
    func updateCurrentTimeOfDevice(characteristic: Characteristic){
        let date = Date()
        let calendar = Calendar.current
        var comp = calendar.dateComponents([.day, .month, .year, .hour, .minute, .second, .weekday, .nanosecond], from: date)
        
        let monthVal = comp.month!
        
        let year1 = Int8(bitPattern: UInt8((comp.year!) & 0xFF))
        let year2 = Int8(bitPattern: UInt8(((comp.year!) >> 8) & 0xFF))
        let month = Int8(bitPattern: UInt8(monthVal))
        let day = Int8(bitPattern: UInt8(comp.day!))
        let hour = Int8(bitPattern: UInt8(comp.hour!))
        let min = Int8(bitPattern: UInt8(comp.minute!))
        let sec = Int8(bitPattern: UInt8(comp.second!))
        let weekday = Int8(bitPattern: UInt8(comp.weekday!))
        let quiterval = Int8(bitPattern: UInt8(0))
        let adjustReason = Int8(bitPattern: UInt8(1))

        let isCurrentTime = characteristic.uuid.uuidString == CharacteristicUUIDs.currentTime().nr
        
        var data = [year1, year2, month, day ,hour ,min ,sec]
        let currentTimeArray = [weekday , quiterval , adjustReason];
        if(isCurrentTime) {data.append(contentsOf:currentTimeArray)}
        //return currentTimeArray//Data(bytes: currentTimeArray, count: currentTimeArray.count)
        //let charIdentifier = CharacteristicIdentifier(characteristic: CBUUID(string: characteristic.nr), service: CBUUID(string: service.nr))
        characteristic.writeValue(Data(bytes: data, count:data.count), type: .withResponse).asObservable().subscribe(onNext:{c in print("Write success! \(c.value)")
            logger().info(str: "Time updated successfully")
        }, onError: {
            e in print("write failed!\(e)")
            logger().info(str: "Time update failed")
        }, onCompleted: {print("write completed") }, onDisposed: {print("write disposed")})
        //peripheral.writeValue(data:data, for: peripheral.characteristic(with: TimeCharacteristic()), type: .withResponse)
    }
    
    func findTimeCharacteristicsFromPeripheral(peripheral: Peripheral) {
        peripheral.discoverServices(self.services)
        .asObservable()
        .flatMap { Observable.from($0) }
        .flatMap { $0.discoverCharacteristics(self.timeCharacteristics)}.asObservable()
        .flatMap { Observable.from($0) }
        .subscribe(onNext: { characteristic in
            self.updateCurrentTimeOfDevice(characteristic:characteristic)
            })
    }
    
    func connectToDevice(deviceDescription: PeripheralDescription) {
        logger().info(str: ("connecting to \(deviceDescription.description())"))
        let device = discoveredDevices.first(where: {p in p.identifier.uuidString == deviceDescription.UUID})
        if(device != nil) {
            
            let peripheralDescription = PeripheralDescription(
                UUID: device?.identifier.uuidString ?? "", name: device?.name ?? "unknown")
            
            device?.establishConnection().flatMap {
                peripheral -> Single<[Service]> in
                
                logger().info(str: ("connected to \(deviceDescription.name)"))
                
                //self.findTimeCharacteristicsFromPeripheral(peripheral: peripheral)
                
                return peripheral.discoverServices(self.services)
                
            }.asObservable()
            .flatMap { Observable.from($0) }
            .flatMap { $0.discoverCharacteristics(self.characteristics)}.asObservable()
            .flatMap { Observable.from($0) }
                .subscribe(onNext: { characteristic in
                print("found characteristic: \(characteristic.characteristic.description)")
                
                    if(characteristic.uuid.uuidString == CharacteristicUUIDs.dateTime().nr || characteristic.uuid.uuidString == CharacteristicUUIDs.currentTime().nr) {
                        self.updateCurrentTimeOfDevice(characteristic: characteristic)
                    }
                    
                self.onCharacteristicDiscovered(
                    peripheralDescription,
                    CharacteristicUUIDs.Companion().fromNr(nr: characteristic.uuid.uuidString),
                    ServiceUUID.Companion().fromNr(nr: characteristic.service.uuid.uuidString) ?? ServiceUUID.unknown()
                )
                
                if(characteristic.properties.contains(.notify) || characteristic.properties.contains(.indicate)) {
                    characteristic.observeValueUpdateAndSetNotification()
                        .delaySubscription(RxTimeInterval.seconds(2), scheduler: MainScheduler.instance)
                    .subscribe(onNext: {
                        let newValue = $0.value ?? Data(capacity: 0)
                         print("characteristic update: \(newValue)")
                        self.onResult(
                                self.messageParser.packageBleReading(
                                    data: newValue as Data,
                                    device: device?.peripheral,
                                    characteristic:characteristic.characteristic)
                            )
                        })
                } else if(characteristic.properties == CBCharacteristicProperties.read) {
                    print("read characteristic: \(characteristic.characteristic.description)")
                    characteristic.readValue().asObservable().subscribe(onNext: {
                        let data = $0.value
                        self.onResult(
                            self.messageParser.packageBleReading(
                                data: data,
                                device: device?.peripheral,
                                characteristic:characteristic.characteristic)
                        )
                    })
                }
                })
        }
    }
    
    func scanForDevices() {
        self.discoveredDevices = []
        if(manager.state != .poweredOn) {
            print("not powered on")
        } else {
            currentDiscovery?.dispose()
            logger().info(str: "Scanning for devices")
            currentDiscovery = manager.scanForPeripherals(withServices: services)
                .subscribe(onNext: { scannedPeripheral in
                    print("device discovered \(scannedPeripheral.peripheral)")
                    self.onDiscover(self.peripheralToDescription(peripheral: scannedPeripheral.peripheral))
                    self.discoveredDevices.append(scannedPeripheral.peripheral)
                    print("id: \(scannedPeripheral.peripheral.identifier.uuidString)")
                }, onDisposed: {logger().info(str: "Stopping scan")})
        }
    }
    
    func peripheralToDescription(peripheral : Peripheral) -> PeripheralDescription{
        return PeripheralDescription(UUID: peripheral.peripheral.identifier.uuidString, name: peripheral.peripheral.name ?? "unknown")
    }
}

class ViewController: UIViewController {
    let rxtest : RxTest! = RxTest()
    var deviceCentral : DeviceCentral!

    override func viewDidLoad() {
        super.viewDidLoad()
        text.isScrollEnabled = true
        text.isUserInteractionEnabled = true
        //addToTextView(txt: "test")
        Sample().checkMe(fn: addToTextView)
        //logger().nsdata.compareAndSet(expected:logger().additionalAction.value, new: NSData(bytes:[0,1,1] as [UInt8], length: 1))
        GlobalSingleton().initializeGlobals(printToScreen: addToTextView, nsdData: NSData(bytes:[1,1] as [UInt8], length: 2), queue: DispatchQueue(label: "tiny.angry.kitten.concurrentQUeueueueue", attributes: .concurrent))
        deviceCentral =  DeviceCentral(bleCentral: rxtest)
    }
    
    func addToTextView(txt : String) {
          text.text += txt
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBAction func connectToMeter(_ sender: Any) {
        deviceCentral.connectToDevice(
            device: PeripheralDescription(
                UUID: "FC637664-D732-5B33-193A-8DF96288E3A6", name: "Glucometerthingy"
            )
        )
    }
    @IBAction func connectToBp(_ sender: Any) {
        deviceCentral.connectToDevice(
            device: PeripheralDescription(
                UUID: "552D7A89-7BB7-C25D-6936-5AF9C752CC03", name: "Bp A and D"
            )
        )
    }
    @IBOutlet weak var text: UITextView!
    @IBAction func scan(_ sender: Any) {
        logger().debug(str: "scan pressed")
        deviceCentral.scanForDevices()
    }
    @IBAction func connectToTickr(_ sender: Any) {
        deviceCentral.connectToDevice(device: PeripheralDescription(UUID: "1E2E02D0-7FD9-5AF3-5B7A-8C1ECD50277D", name: "tickr"))
    }
    
    @IBAction func connectToThermometer(_ sender: Any) {
        deviceCentral.connectToDevice(device: PeripheralDescription(UUID: "F5727898-9CB4-51EA-9554-DB7AA6304A24", name: "Thermometer"))
    }
}
