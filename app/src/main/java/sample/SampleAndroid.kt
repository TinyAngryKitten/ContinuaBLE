package sample

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import ble.BLECentral
import ble.BluetoothController
import ble.DeviceCentral
import data.PeripheralDescription
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val turnOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(turnOn, 0)
        /*val turnDiscoverable = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(turnDiscoverable, 0)*/


        val controller = BluetoothController.create(this)
        val deviceCentral = DeviceCentral(BLECentral(controller!!))

        //controller?.adapter?.bluetoothLeScanner.startScan()
        //initiate the bluetooth controller
        setContentView(R.layout.activity_main)
        val scanBtn = findViewById<Button>(R.id.scan)
        val conBtn = findViewById<Button>(R.id.connect)
        val textfield = findViewById<TextView>(R.id.text)
        logger.additionalAction = {str-> textfield.append(str)}

        conBtn.setOnClickListener {
            //weight: B4:99:4C:5B:FA:0F
            //weight main: 10:CE:A9:C9:78:64
            //glucosemeter: "34:03:DE:0D:51:16"
            //gluco2: "F0:B5:D1:58:7D:56"
            //bp: 18:93:D7:7A:40:DC
            deviceCentral.connectToDevice(PeripheralDescription("18:93:D7:7A:40:DC"))
        }//("9A427AF2-7BC3-785F-471E-CF3AA62E0A5A") }
        scanBtn.setOnClickListener {
            controller.scan()
        }
    }
}