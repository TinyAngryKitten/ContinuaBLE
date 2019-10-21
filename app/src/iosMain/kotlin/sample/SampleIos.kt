package sample

import ble.BluetoothController
import iso.*
import kotlin.native.concurrent.freeze

actual class Sample {
    actual fun checkMe(fn : (String) -> Unit) {
        logger.additionalAction.compareAndSwap(logger.additionalAction.value,fn)

        BluetoothController(
            listOf(
                glucoseServiceUUID,
                weightServiceUUID,
                deviceInformationServiceUUID,
                batteryServiceUUID),	
            listOf(
                glucoseFeatureCharacteristic,
                glucoseMeasurementCharacteristic,
                glucoseMeasurementContextCharacteristic
            )
        )
    }
}



 class Logger(@SharedImmutable val log : (String)-> Unit) {
    fun println(str : String) {
        log(str)
        print(str+"\n")
    }
}
actual object Platform {
    actual val name: String = "iOS"
}
