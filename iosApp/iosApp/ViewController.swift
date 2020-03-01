import UIKit
import app
//glucose: FC637664-D732-5B33-193A-8DF96288E3A6
//bp: 552D7A89-7BB7-C25D-6936-5AF9C752CC03

class ViewController: UIViewController {
    let deviceCentral = DeviceCentral(bleCentral: BLECentral())
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addToTextView(txt: "test")
        //Sample().checkMe(fn: addToTextView)
        /*logger().nsdata.compareAndSet(expected:logger().additionalAction.value, new: NSData(bytes:[0,1,1] as [UInt8], length: 1))*/
        GlobalSingleton().initializeGlobals(printToScreen: addToTextView, nsdData: NSData(bytes:[1,1] as [UInt8], length: 2), queue: DispatchQueue(label: "tiny.angry.kitten.concurrentQUeueueueue", attributes: .concurrent))
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
    
    
}
