import UIKit
import app

class ViewController: UIViewController {
    let deviceCentral = DeviceCentral(bleCentral: BLECentral())
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Sample().checkMe(fn: addToTextView)
        /*logger().nsdata.compareAndSet(expected:logger().additionalAction.value, new: NSData(bytes:[0,1,1] as [UInt8], length: 1))*/
    }
    
    func addToTextView(txt : String) {
          text.text += txt
        print("TextPrinted:"+txt);
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBAction func connectToMeter(_ sender: Any) {
        logger().info(str: "Connecting to meter")
        deviceCentral.connectToDevice(device: PeripheralDescription(
            UUID: "FC637664-D732-5B33-193A-8DF96288E3A6", name: "Glucometerthingy"
        ))
        
    }
    @IBOutlet weak var text: UITextView!
    @IBAction func scan(_ sender: Any) {
        logger().debug(str: "scan pressed")
        deviceCentral.scanForDevices()
    }
    
    
}
