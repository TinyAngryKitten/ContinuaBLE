import UIKit
import app

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        Sample().checkMe(fn: addToTextView)
    }
    
    func addToTextView(txt : String) {
          text.text += txt
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBAction func connectToMeter(_ sender: Any) {
        logger().debug(str: "Connecting to meter")
        BLEManager().connectToDevice(deviceDescription: PeripheralDescription(
            UUID: "9A427AF2-7BC3-785F-471E-CF3AA62E0A5A", name: "Glucometerthingy"
        ))
    }
    @IBOutlet weak var text: UITextView!
    @IBAction func scan(_ sender: Any) {
        logger().debug(str: "scan pressed")
        Sample().checkMe(fn: addToTextView)
    }
    
    
}
