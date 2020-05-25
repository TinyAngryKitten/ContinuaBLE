package view

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import ble.AndroidBLECentral
import ble.BluetoothController
import ble.DeviceCentral
import bledata.PeripheralDescription
import util.logger

//weight: B4:99:4C:5B:FA:0F
//weight main: 10:CE:A9:C9:78:64
//glucosemeter: "34:03:DE:0D:51:16"
//gluco2: "F0:B5:D1:58:7D:56"
//bp: 18:93:D7:7A:40:DC
//ticker: C7:38:EE:BB:77:2C
//thermometer: 6C:EC:EB:43:E0:65

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val turnOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(turnOn, 0)
        /*val turnDiscoverable = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(turnDiscoverable, 0)*/


        val controller = BluetoothController.create(this)
        val deviceCentral = DeviceCentral(AndroidBLECentral(controller!!))

        //controller?.adapter?.bluetoothLeScanner.startScan()
        //initiate the bluetooth controller
        setContentView(R.layout.activity_main)
        val scanBtn = findViewById<Button>(R.id.scan)
        val glucoBtn = findViewById<Button>(R.id.glucose)
        val thermoBtn = findViewById<Button>(R.id.thermometer)
        val BPBtn = findViewById<Button>(R.id.bp)
        val tickrBtn = findViewById<Button>(R.id.tickr)
        val textfield = findViewById<TextView>(R.id.text)
        logger.additionalAction = { str-> textfield.append(str)}

        glucoBtn.setOnClickListener {
            deviceCentral.connectToDevice(PeripheralDescription("6C:EC:EB:43:E0:65"))
        }//("9A427AF2-7BC3-785F-471E-CF3AA62E0A5A") }
        tickrBtn.setOnClickListener {
            deviceCentral.connectToDevice(PeripheralDescription("C7:38:EE:BB:77:2C"))
        }
        thermoBtn.setOnClickListener {
            deviceCentral.connectToDevice(PeripheralDescription("6C:EC:EB:43:E0:65"))
        }
        BPBtn.setOnClickListener {
            deviceCentral.connectToDevice(PeripheralDescription("18:93:D7:7A:40:DC"))
        }
        scanBtn.setOnClickListener {
            controller.scan()
        }
    }
}