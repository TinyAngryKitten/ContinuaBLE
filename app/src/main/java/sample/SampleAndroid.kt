package sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import ble.BLECentral
import ble.BluetoothController
import ble.DeviceCentral
import data.PeripheralDescription
import iso.parse
import iso.parseBLEReading

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val controller = BluetoothController.create(this)
        val deviceCentral = DeviceCentral(BLECentral(controller!!))

        //controller?.adapter?.bluetoothLeScanner.startScan()
        //initiate the bluetooth controller
        setContentView(R.layout.activity_main)
        val scanBtn = findViewById<Button>(R.id.scan)
        val conBtn = findViewById<Button>(R.id.connect)

        conBtn.setOnClickListener { deviceCentral.connectToDevice(PeripheralDescription("34:03:DE:0D:51:16"))}//("9A427AF2-7BC3-785F-471E-CF3AA62E0A5A") }
        scanBtn.setOnClickListener { controller?.scan() }
    }
}