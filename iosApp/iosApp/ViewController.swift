import UIKit
import app

class ViewController: UIViewController {
    let bleCentral : SwiftBLECentral! = SwiftBLECentral()
    var deviceCentral : DeviceCentral!

    override func viewDidLoad() {
        super.viewDidLoad()
        text.isScrollEnabled = true
        text.isUserInteractionEnabled = true
        logger().setAddToView(fn:addToTextView)
        deviceCentral =  DeviceCentral(bleCentral: bleCentral)
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
