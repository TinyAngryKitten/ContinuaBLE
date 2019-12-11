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
                batteryServiceUUID,
                healthServiceUUID,
                stepServiceUUID,
                sleepServiceUUID,
                enhancedHeartRateServiceUUID
            ),
            listOf(
                glucoseFeatureCharacteristic,
                glucoseMeasurementCharacteristic,
                glucoseMeasurementContextCharacteristic,
                modelNumber,
                manufacturerName,
                currentTime,
                batteryLevel,
                ehancedHeartRate,
                enhancedHeartRateFeature
            )
        )
    }
}


val modelNumber = "0x2A24"
val manufacturerName = "0x2A29"

val currentTime = "0x2A2B"
val batteryLevel = "0x2A19"

val ehancedHeartRate = "0x0101"
val enhancedHeartRateFeature = "0x0102"



 class Logger(@SharedImmutable val log : (String)-> Unit) {
    fun println(str : String) {
        log(str)
        print(str+"\n")
    }
}
actual object Platform {
    actual val name: String = "iOS"
}
