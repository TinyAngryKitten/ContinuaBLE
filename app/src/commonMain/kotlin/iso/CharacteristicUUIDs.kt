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

sealed class CharacteristicUUIDs(val id : String,val service: ServiceUUID, val parse : (BLEReading) -> DataRecord) {
    val nr = id.substring(2)

    override fun toString(): String {
        return this::class.simpleName ?: "Unknown characteristic"
    }

    //GLUCOSE
    object glucoseFeature : CharacteristicUUIDs("0x2A51", ServiceUUID.glucose, ::parseGlucoseFeatures)
    object glucoseMeasurement : CharacteristicUUIDs("0x2A18", ServiceUUID.glucose,::parseGlucoseReading)
    object glucoseMeasurementContext : CharacteristicUUIDs("0x2A34",ServiceUUID.glucose,::parseGlucoseContextReading)
    object recordControlPoint : CharacteristicUUIDs("0x2A52", ServiceUUID.glucose, ::parseRecordControlPoint)

    //HEART RATE
    object heartRateMeasurement : CharacteristicUUIDs("0x2A37",ServiceUUID.heartRate,::parseHeartRateMeasurement)
    object bodySensorLocation : CharacteristicUUIDs("0x2A38",ServiceUUID.heartRate,::parseBodySensorLocation)
    //object heartRateControlPoint : CharacteristicUUIDs("2A39",)

    //BLOOD PRESSURE
    object bloodPressureFeature : CharacteristicUUIDs("0x2A49",ServiceUUID.bloodPressure,::parseBloodPressureFeature)
    object bloodPressureMeasurement : CharacteristicUUIDs("0x2A35",ServiceUUID.bloodPressure,::parseBloodPressureMeasurement)
    object IntermediateCuffPressure : CharacteristicUUIDs("0x2A36", ServiceUUID.bloodPressure, ::intermediateCuffPressureParser)

    //BODY WEIGHT
    object weightFeature : CharacteristicUUIDs("0x2A9E", ServiceUUID.weight,::parseWeightScaleFeature)
    object weightMeasurement : CharacteristicUUIDs("0x2A9D",ServiceUUID.weight,::parseWeightMeasurement)

    //DEVICE INFO
    object modelNumber : CharacteristicUUIDs("0x2A24",ServiceUUID.deviceInformation,::parseModelNumber)
    object serialNumber : CharacteristicUUIDs("0x2A25",ServiceUUID.deviceInformation,::parseSerialNumber)
    object firmwareRevision : CharacteristicUUIDs("0x2A26",ServiceUUID.deviceInformation,::parseFirmwareRevision)
    object hardwareRevision : CharacteristicUUIDs("0x2A27",ServiceUUID.deviceInformation,::parseHardwareRevision)
    object softwareRevision : CharacteristicUUIDs("0x2A28",ServiceUUID.deviceInformation,::parseSoftwareRevision)
    object manufacturerName :CharacteristicUUIDs("0x2A29",ServiceUUID.deviceInformation,::parseManufacturerName)

    object temperatureMeasurement : CharacteristicUUIDs("0x2A1C", ServiceUUID.thermometer, ::parseTemperatureMeasurement)

    object plxSpotCheck : CharacteristicUUIDs("0x2A5E", ServiceUUID.pulseOximeter,::parsePlxSpotCheck)
    object plxContinousMeasurement: CharacteristicUUIDs("0x2A5F", ServiceUUID.pulseOximeter,::parseContinousPlxMeasurement)
    //object plxFeatures : CharacteristicUUIDs("0x2A60",)

    //CURRENT TIME
    object currentTime : CharacteristicUUIDs("0x2A2B",ServiceUUID.currentTime,::parseCurrentTime)
    object dateTime : CharacteristicUUIDs("0x2A08",ServiceUUID.currentTime,::parseDateTime)

    //BATTERY LEVEL
    object batteryLevel : CharacteristicUUIDs("0x2A19",ServiceUUID.battery,::parseBatteryLevel)

    class UnsupportedCharacteristic(id: String,device : PeripheralDescription) : CharacteristicUUIDs(id,ServiceUUID.unknown,{EmptyRecord(device)}) {
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
                recordControlPoint,

                heartRateMeasurement,
                bodySensorLocation,

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
                plxContinousMeasurement,
                plxSpotCheck,

                batteryLevel,
                currentTime,
                dateTime
            )

        fun fromNr(nr : String) = getAll().find { it.nr.equals(nr,ignoreCase = true) } ?: UnsupportedCharacteristic("${unsupportedCharacteristicName("0x$nr")}",PeripheralDescription(""))
        fun fromId(id : String) = getAll().find { it.id.equals(id,ignoreCase = true) } ?: UnsupportedCharacteristic(
            unsupportedCharacteristicName(id),PeripheralDescription(""))

        fun unsupportedCharacteristicName(id : String) = when(id.toUpperCase()){
            "0X2A23" -> "$id (System ID)"
            "0X2A50" -> "$id (PnP ID)"
            "0X2A2A" -> "$id (Regulatory Certificate Lists)"
            else -> id
        }
    }
}

