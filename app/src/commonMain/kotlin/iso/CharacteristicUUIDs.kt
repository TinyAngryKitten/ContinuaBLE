package iso

import bledata.BLEReading
import data.DataRecord
import data.EmptyRecord
import data.PeripheralDescription
import iso.services.*
import parseWeightMeasurement
import parseWeightScaleFeature

//IDs for characteristics defined in the bluetooth specification at:
//https://www.bluetooth.com/specifications/gatt/characteristics/

//glucose characteristics

sealed class CharacteristicUUIDs(val id : String,val parse : (BLEReading) -> DataRecord) {
    val nr = id.substring(2)

    //GLUCOSE
    object glucoseFeature : CharacteristicUUIDs("0x2A51", ::parseGlucoseFeatures)
    object glucoseMeasurement : CharacteristicUUIDs("0x2A18", ::parseGlucoseReading)
    object glucoseMeasurementContext : CharacteristicUUIDs("0x2A34",::parseGlucoseContextReading)

    //HEART RATE
    object heartRateMeasurement : CharacteristicUUIDs("0x2A37",::parseHeartRateMeasurement)
    object bodySensorLocation : CharacteristicUUIDs("0x2A38",::parseBodySensorLocation)
    //object heartRateControlPoint : CharacteristicUUIDs("2A39",)

    //BLOOD PRESSURE
    object bloodPressureFeature : CharacteristicUUIDs("0x2A49",::parseBloodPressureFeature)
    object bloodPressureMeasurement : CharacteristicUUIDs("0x2A35",::parseBloodPressureMeasurement)
    //object IntermediateCuffPressure : CharacteristicUUIDs("0x2A36")

    //BODY WEIGHT
    object weightFeature : CharacteristicUUIDs("0x2A9E", ::parseWeightScaleFeature)
    object weightMeasurement : CharacteristicUUIDs("0x2A9D",::parseWeightMeasurement)

    //DEVICE INFO
    object modelNumber : CharacteristicUUIDs("0x2A24",::parseModelNumber)
    object serialNumber : CharacteristicUUIDs("0x2A25",::parseSerialNumber)
    object firmwareRevision : CharacteristicUUIDs("0x2A26",::parseFirmwareRevision)
    object hardwareRevision : CharacteristicUUIDs("0x2A27",::parseHardwareRevision)
    object softwareRevision : CharacteristicUUIDs("0x2A28",::parseSoftwareRevision)
    object manufacturerName :CharacteristicUUIDs("0x2A29",::parseManufacturerName)

    object temperatureMeasurement : CharacteristicUUIDs("0x2A1C", ::parseTemperatureMeasurement)

    //CURRENT TIME
    //object currentTime : CharacteristicUUIDs("0x2A2B",::parseCurrentTime)

    //BATTERY LEVEL
    object batteryLevel : CharacteristicUUIDs("0x2A19",::parseBatteryLevel)

    class UnsupportedCharacteristic(id: String,device : PeripheralDescription) : CharacteristicUUIDs(id,{EmptyRecord(device)}) {
        override fun equals(other: Any?): Boolean {
            return if(other is UnsupportedCharacteristic) id.equals(other.id,ignoreCase = true)
            else false
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "Unsupported Characteristic, UUID: $id"
        }
    }

    companion object {
        fun getAll() : List<CharacteristicUUIDs> = listOf(
                glucoseFeature,
                glucoseMeasurement,
                glucoseMeasurementContext,

                heartRateMeasurement,
                bodySensorLocation,
                //heartRateControlPoint,

                bloodPressureFeature,
                bloodPressureMeasurement,

                weightFeature,
                weightMeasurement,

                modelNumber,
                serialNumber,
                firmwareRevision,
                hardwareRevision,
                softwareRevision,
                manufacturerName,

                temperatureMeasurement,

                //currentTime,
                batteryLevel
            )

        fun fromNr(nr : String) = getAll().find { it.nr.equals(nr,ignoreCase = true) } ?: UnsupportedCharacteristic("${unsupportedCharacteristicName("0x$nr")}",PeripheralDescription("unknown"))
        fun fromId(id : String) = getAll().find { it.id.equals(id,ignoreCase = true) } ?: UnsupportedCharacteristic(
            unsupportedCharacteristicName(id),PeripheralDescription("unknown"))

        fun unsupportedCharacteristicName(id : String) = when(id){
            "0x2A2B" -> "$id (Current time)"
            "0x2A23" -> "$id (System ID)"
            "0x2A50" -> "$id (PnP ID)"
            "0x2A08" -> "$id (Date Time)"
            "0x2A52" -> "$id (Record Access control point)"
            else -> id
        }
    }
}

