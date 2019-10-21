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
    @IBOutlet weak var text: UITextView!
}
