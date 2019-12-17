package data

import iso.ISOValue


sealed class DataRecord

object EmptyRecord : DataRecord()

data class GlucoseRecord(
    val unit : BloodGlucoseUnit,
    val amount : Float,
    val sequenceNumber : UInt,
    val context : GlucoseRecordContext?
) : DataRecord() {
    companion object {
        fun fromISOValues(
            unit : ISOValue.Flag,
            amount : ISOValue.SFloat?,
            sequenceNumber : ISOValue.UInt16,
            context : GlucoseRecord?
        ) : GlucoseRecord? {
            return GlucoseRecord(
                if(unit.value) BloodGlucoseUnit.MMOL else BloodGlucoseUnit.DL,
                if(amount is ISOValue.SFloat.Value) amount.value else return null,
                sequenceNumber.value,
                null
            )
        }
    }
}

//These are all UTF8 strings
sealed class DeviceInfo : DataRecord() {
    class ModelNumber(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
    class SerialNumber(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
    class FirmwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
    class HardwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
    class SoftwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
    class ManufacturerName(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfo()
}

class BatteryLevel(
    level : Int,
    device : PeripheralDescription
) : DataRecord() {
    constructor(level: ISOValue.UInt8, device: PeripheralDescription) : this(level.value.toInt(), device)
}

class BloodPressureFeatures(
    val bodyMovementDetection : Boolean,
    val cuffFitDetection : Boolean,
    val irregularPulseDetection : Boolean,
    val pulseRateRangeDetection : Boolean,
    val measurementPositionDetection : Boolean,
    val multipleBondSupport : Boolean
) : DataRecord()

class BloodPressureRecord(
    val systolic : Float,
    val diastolic : Float,
    val meanArtieralPressure : Float,
    val unit : BloodPressureUnit,
    val timeStamp : ISOValue.DateTime?,
    val bpm : Float?,
    val userId : Int?,
    val status : MeasurementStatus?
) : DataRecord(){

    companion object {
        fun fromISOValues(
            systolic: ISOValue.SFloat,
            diastolic: ISOValue.SFloat,
            meanArtieralPressure: ISOValue.SFloat,
            unit: BloodPressureUnit,
            timeStamp: ISOValue.DateTime?,
            bpm: ISOValue.SFloat?,
            userId: ISOValue.UInt8?,
            status: MeasurementStatus?
        ): BloodPressureRecord? {
            if (systolic !is ISOValue.SFloat.Value) return null
            if (diastolic !is ISOValue.SFloat.Value) return null
            if (meanArtieralPressure !is ISOValue.SFloat.Value) return null
            if (bpm != null && bpm !is ISOValue.SFloat.Value) return null

            return BloodPressureRecord(
                systolic.value,
                diastolic.value,
                meanArtieralPressure.value,
                unit,
                timeStamp,
                (bpm as ISOValue.SFloat.Value).value,
                userId?.value?.toInt(),
                status
            )
        }
    }
}

class MeasurementStatus()

data class BodyCompositionFeature(
    val timeStamp : Boolean,
    val multipleUsers : Boolean,
    val basalMetabolism : Boolean,
    val musclePercent : Boolean,
    val muscleMass : Boolean,
    val fatFreeMass : Boolean,
    val softLeanMass : Boolean,
    val bodyWaterMass : Boolean,
    val impedance : Boolean,
    val weight : Boolean,
    val height : Boolean,
    val massMeasurementResolution: WeightMeasurementResolution,
    val heightMeasurementResolution: HeightMeasurementResolution
) : DataRecord()

data class BodyCompositionRecord(
   val bodyFatPercent : UInt,
   val dateTime: ISOValue.DateTime?,
   val userId: UInt?,
   val basalMetabolism : UInt?,
   val musclePercent : UInt?,
   val muscleMass : UInt?,
   val fatFreeMass : UInt?,
   val softLeanMass : UInt?,
   val bodyWaterMass : UInt?,
   val impedance: UInt?
   //val weight : UInt,
   //val height : UInt
) : DataRecord() {
    constructor(bodyFatPercent : ISOValue.UInt16,
                dateTime: ISOValue.DateTime?,
                userId: ISOValue.UInt8?,
                basalMetabolism : ISOValue.UInt16?,
                musclePercent : ISOValue.UInt16?,
                muscleMass : ISOValue.UInt16?,
                fatFreeMass : ISOValue.UInt16?,
                softLeanMass : ISOValue.UInt16?,
                bodyWaterMass : ISOValue.UInt16?,
                impedance: ISOValue.UInt16?)
            : this(bodyFatPercent.value,
                dateTime,
                userId?.value,
                basalMetabolism?.value,
                musclePercent?.value,
                muscleMass?.value,
                fatFreeMass?.value,
                softLeanMass?.value,
                bodyWaterMass?.value,
                impedance?.value)
}



data class WeightRecord(
    val weight: UInt,
    val weightUnit: WeightUnit,
    val timestamp : ISOValue.DateTime?,
    val userId: UInt?,
    val BMI : UInt?,
    val height: UInt?,
    val heightUnit: LengthUnit?
) : DataRecord() {
    companion object{
        fun fromISOValues(
            weight : ISOValue.UInt16,
            weightUnit: WeightUnit,
            timeStamp: ISOValue.DateTime?,
            userId: ISOValue.UInt8?,
            BMI: ISOValue.UInt16?,
            height: ISOValue.UInt16?,
            heightUnit: LengthUnit?
        ) : WeightRecord? {
            if(height != null && heightUnit == null) return null
            return WeightRecord(
                weight.value,
                weightUnit,
                timeStamp,
                userId?.value,
                BMI?.value,
                height?.value,
                heightUnit
            )
        }
    }
}

data class WeightFeatures(
    val timeStamp : Boolean,
    val multipleUsers : Boolean,
    val BMI : Boolean,
    val weightMeasurementResolution: WeightMeasurementResolution,
    val heightMeasurementResolution : HeightMeasurementResolution
) : DataRecord()

/**
 * Defines the resolution of height measurements for both kg and lb
 */
sealed class WeightMeasurementResolution(val kg: Float, val lb : Float) {
    object NotSpecified : WeightMeasurementResolution(Res7.kg,Res7.lb)//not specified, use default
    object Res1 : WeightMeasurementResolution(0.5f,1f)
    object Res2 : WeightMeasurementResolution(0.2f,0.5f)
    object Res3 : WeightMeasurementResolution(0.1f,0.2f)
    object Res4 : WeightMeasurementResolution(0.05f,0.1f)
    object Res5 : WeightMeasurementResolution(0.02f,0.05f)
    object Res6 : WeightMeasurementResolution(0.01f,0.02f)
    object Res7 : WeightMeasurementResolution(0.005f,0.01f)//default

    companion object{
        fun fromInt(i : Int) = when(i) {
            1 -> Res1
            2 -> Res2
            3 -> Res3
            4 -> Res4
            5 -> Res5
            6 -> Res6
            7 -> Res7
            else -> NotSpecified
        }
    }
}


/**
 * Defines the resolution of height measurements for both cm and inches
 */
sealed class HeightMeasurementResolution(val increments : Float) {
    object NotSpecified : HeightMeasurementResolution(0f)
    object LowRes : HeightMeasurementResolution(0.01f)
    object MediumRes : HeightMeasurementResolution(0.005f)
    object HighRes : HeightMeasurementResolution(0.001f)

    companion object{
        fun fromInt(i : Int) = when(i) {
            1 -> LowRes
            2 -> MediumRes
            3 -> HighRes
            else -> NotSpecified
        }
    }
}

data class HeartRateRecord(
    val measurementValue : UInt,
    val energyExpended : UInt?,
    val sensorContact : SensorContact,
    val rrInterval : UInt?

) : DataRecord() {
    constructor(
        measurementValue: ISOValue.UInt8,
        energyExpended: ISOValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: ISOValue.UInt16?
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.value)

    constructor(
        measurementValue: ISOValue.UInt16,
        energyExpended: ISOValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: ISOValue.UInt16?
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.value)
}

sealed class SensorContact {
    object NotSupported : SensorContact()
    object ContactNotDetected : SensorContact()
    object ContactDetected : SensorContact()
}

data class BodySensorLocationRecord(
    val location : BodySensorLocation,
    val device: PeripheralDescription
) : DataRecord()

sealed class BodySensorLocation {
    object Other : BodySensorLocation()
    object Chest : BodySensorLocation()
    object Wrist : BodySensorLocation()
    object Finger : BodySensorLocation()
    object Hand : BodySensorLocation()
    object EarLobe : BodySensorLocation()
    object Foot : BodySensorLocation()
}

//TODO: support this maybe? unknown if any glucose meter actually support it or if it would be useful
class GlucoseRecordContext : DataRecord() {}