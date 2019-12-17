package iso

//IDs for characteristics defined in the bluetooth specification at:
//https://www.bluetooth.com/specifications/gatt/characteristics/

//glucose characteristics

sealed class CharacteristicUUIDs(val id : String) {
    val nr = id.substring(2)
    //GLUCOSE
    object glucoseFeature : CharacteristicUUIDs("0x2A51")
    object glucoseMeasurement : CharacteristicUUIDs("0x2A18")
    object glucoseMeasurementContext : CharacteristicUUIDs("0x2A34")

    //HEART RATE
    object heartRateMeasurement : CharacteristicUUIDs("0x2A37")
    object bodySensorLocation : CharacteristicUUIDs("2A38")
    object heartRateControlPoint : CharacteristicUUIDs("2A39")

    //BLOOD PRESSURE
    object bloodPressureFeature : CharacteristicUUIDs("0x2A49")
    object bloodPressureMeasurement : CharacteristicUUIDs("0x2A35")
    //object IntermediateCuffPressure : CharacteristicUUIDs("0x2A36")

    //BODY WEIGHT
    object weightFeature : CharacteristicUUIDs("0x2A9E")
    object weightMeasurement : CharacteristicUUIDs("0x2A9D")

    //DEVICE INFO
    object modelNumber : CharacteristicUUIDs("0x2A24")
    object serialNumber : CharacteristicUUIDs("0x2A25")
    object firmwareRevision : CharacteristicUUIDs("0x2A26")
    object hardwareRevision : CharacteristicUUIDs("0x2A27")
    object softwareRevision : CharacteristicUUIDs("0x2A28")
    object manufacturerName :CharacteristicUUIDs("0x2A29")

    //CURRENT TIME
    object currentTime : CharacteristicUUIDs("0x2A2B")
    //BATTERY LEVEL
    object batteryLevel : CharacteristicUUIDs("0x2A19")

    //SAMSUNG HEALTH
    object ehancedHeartRate : CharacteristicUUIDs("0x0101")
    object enhancedHeartRateFeature : CharacteristicUUIDs("0x0102")
}