import UIKit
import app
//import Bluejay
import CoreBluetooth
import RxBluetoothKit
import RxSwift
//glucose: FC637664-D732-5B33-193A-8DF96288E3A6
//bp: 552D7A89-7BB7-C25D-6936-5AF9C752CC03

/*struct HeartRateMeasurement: Receivable {
    init(bluetoothData: Data) throws {
        print("bluetooth data size: \(bluetoothData.count)")
    }

}*/
/*
class BlueSwiftTest {
    let services = ServiceUUID.Companion().getAll().map({c in ServiceIdentifier(uuid: c.nr)})
    let characteristics = CharacteristicUUIDs.Companion().getAll().map{c in CharacteristicIdentifier(uuid: c.nr, service:ServiceIdentifier(uuid: c.service.nr))}
    
    var bps : ServiceIdentifier
    var bp : CharacteristicIdentifier
    
    
    init() {
        bps = ServiceIdentifier(uuid: "1810")
        bp = CharacteristicIdentifier(uuid: "2A35", service: bps)
    }
    var discoveredDevices = [ScanDiscovery]()
    
    func scan() {
        print("scan starting")
    bluejay.scan(
        duration: 15,
        allowDuplicates: false,
        serviceIdentifiers: [bps],//services,
        discovery: { [weak self] (discovery, discoveries) -> ScanAction in
            guard let weakSelf = self else {
                return .stop
            }
            print("device discovered: \(discovery.peripheralIdentifier.description)")

            weakSelf.discoveredDevices.append(discovery)
            weakSelf.bluejay.connect(discovery.peripheralIdentifier, timeout: .seconds(15)) { result in
                switch result {
                case .success:
                    debugPrint("Connection attempt to: \(discovery.peripheralIdentifier.description) is successful")
                    weakSelf.bluejay.listen(to: weakSelf.bp, multipleListenOption: .replaceable)
                    { [weak self] (result: ReadResult<HeartRateMeasurement>) in
                            guard let weakSelf = self else {
                                return
                            }

                            switch result {
                            case .success(let heartRate):
                                print("success!!!")
                            case .failure(let error):
                                debugPrint("Failed to listen with error: \(error.localizedDescription)")
                            }
                    }
                    
                case .failure(let error):
                    debugPrint("Failed to connect with error: \(error.localizedDescription)")
                }
            }

            return .continue
        },
        stopped: { (discoveries, error) in
            if let error = error {
                debugPrint("Scan stopped with error: \(error.localizedDescription)")
            }
            else {
                debugPrint("Scan stopped without error.")
            }
    })
    }
    
    let bluejay = Bluejay()
    func start() {
        bluejay.start()
    }
    func v() {
    }
}
*/

class RxTest : BleCentralInterface{
    
    var onConnect : (PeripheralDescription) -> Void = {_ in print("connect callback not changed")}
    var onDiscover : (PeripheralDescription) -> Void = {_ in print("onDiscover callback not changed")}
    var onResult : (BLEReading) -> Void = {_ in print("on result callback not changed")}
    var stateChanged : (BLEState) -> Void = {_ in print("state changed callback not changed")}
    
    var manager : CentralManager!
    let services = ServiceUUID.Companion().getAll().map({c in CBUUID(string: c.nr)})
    let characteristics = CharacteristicUUIDs.Companion().getAll().map({c in CBUUID(string: c.nr)})
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
    
    func connectToDevice(deviceDescription: PeripheralDescription) {
        print("connecting to \(deviceDescription.description())")
        let device = discoveredDevices.first(where: {p in p.identifier.uuidString == deviceDescription.UUID})
        if(device != nil) {
            device?.establishConnection().flatMap { $0.discoverServices(self.services) }.asObservable()
            .flatMap { Observable.from($0) }
            .flatMap { $0.discoverCharacteristics(self.characteristics)}.asObservable()
            .flatMap { Observable.from($0) }
            .subscribe(onNext: { characteristic in
                print("found characteristic: \(characteristic.characteristic.description)")
                if(characteristic.properties.contains(.notify) || characteristic.properties.contains(.indicate)) {
                    characteristic.observeValueUpdateAndSetNotification().catchError({
                        e in print("ERROR: \(e.localizedDescription)")
                        return Observable.from(characteristic)
                    })
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
        print("scanning for devices")
        if(manager.state != .poweredOn) {
            print("not powered on")
        } else {
            currentDiscovery?.dispose()
            currentDiscovery = manager.scanForPeripherals(withServices: services)
                .subscribe(onNext: { scannedPeripheral in
                    print("device discovered \(scannedPeripheral.peripheral)")
                    self.onDiscover(self.peripheralToDescription(peripheral: scannedPeripheral.peripheral))
                    self.discoveredDevices.append(scannedPeripheral.peripheral)
                })
        }
    }
    
    func peripheralToDescription(peripheral : Peripheral) -> PeripheralDescription{
        return PeripheralDescription(UUID: peripheral.peripheral.identifier.uuidString, name: peripheral.peripheral.name ?? "unknown")
    }
    
    func scan() {
        print("Scan started")
        if(manager.state != .poweredOn) {
            print("not powered on")
        } else {manager.scanForPeripherals(withServices: [bps])
        .subscribe(onNext: { scannedPeripheral in
            print("peripheral discovered: \(scannedPeripheral.peripheral.name)")
            let disposable = scannedPeripheral.peripheral.establishConnection().flatMap { $0.discoverServices([self.bps]) }.asObservable()
                .flatMap { Observable.from($0) }
                .flatMap { $0.discoverCharacteristics([self.bp])}.asObservable()
                .flatMap { Observable.from($0) }
                .subscribe(onNext: { characteristic in
                    print("Discovered characteristic: \(characteristic.characteristic.uuid.uuidString)")
                characteristic.observeValueUpdateAndSetNotification()
                .subscribe(onNext: {
                    let newValue = $0.value
                    print("characteristic\(characteristic.characteristic.uuid.uuidString) update: \(newValue)")
                    self.onResult(
                            self.messageParser.packageBleReading(
                                data: newValue,
                                device: nil,
                                characteristic:characteristic.characteristic)
                        )
                    })
                })
            })
        }}
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
        /*rxtest.connectToDevice(deviceDescription: PeripheralDescription(
            UUID: "552D7A89-7BB7-C25D-6936-5AF9C752CC03", name: "Bp A and D"
        ))*/
    }
    @IBOutlet weak var text: UITextView!
    @IBAction func scan(_ sender: Any) {
        logger().debug(str: "scan pressed")
        deviceCentral.scanForDevices()
        //rxtest.scan()
    }
}
