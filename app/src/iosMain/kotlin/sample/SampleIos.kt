package sample

import ble.BLECentral

class Sample {
    fun checkMe(fn : (String) -> Unit) {
        logger.additionalAction.compareAndSwap(logger.additionalAction.value,fn)
    }
}
