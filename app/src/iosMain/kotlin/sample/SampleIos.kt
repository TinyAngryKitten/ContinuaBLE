package sample

import ble.BLEManager
import ble.BLEState
import ble.BluetoothController
import iso.*

class Sample {
    fun checkMe(fn : (String) -> Unit) {
        logger.additionalAction.compareAndSwap(logger.additionalAction.value,fn)

            BLEManager.scanForDevices()

    }
}
