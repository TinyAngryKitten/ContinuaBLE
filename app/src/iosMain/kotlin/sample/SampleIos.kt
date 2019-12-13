package sample

import ble.BluetoothController
import iso.*

actual class Sample {
    actual fun checkMe(fn : (String) -> Unit) {
        logger.additionalAction.compareAndSwap(logger.additionalAction.value,fn)

        BluetoothController(
            listOf(
                ServiceUUID.glucose,
                ServiceUUID.weight,
                ServiceUUID.deviceInformation,
                ServiceUUID.battery,
                ServiceUUID.health,
                ServiceUUID.step,
                ServiceUUID.sleep,
                ServiceUUID.enhancedHeartRate
            ),
            listOf(
                CharacteristicUUIDs.glucoseFeature,
                CharacteristicUUIDs.glucoseMeasurement,
                CharacteristicUUIDs.glucoseMeasurementContext,
                CharacteristicUUIDs.modelNumber,
                CharacteristicUUIDs.manufacturerName,
                CharacteristicUUIDs.currentTime,
                CharacteristicUUIDs.batteryLevel,
                CharacteristicUUIDs.ehancedHeartRate,
                CharacteristicUUIDs.enhancedHeartRateFeature
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
