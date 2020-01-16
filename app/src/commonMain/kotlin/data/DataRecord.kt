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

//describes a glucse peripherals supported flags
data class GlucoseFeatures (
    val lowBattery : Boolean = false,
    val sensorMalfunction : Boolean = false,
    val sensorSampleSize : Boolean = false,
    val sensorStripInsertionMalfunction : Boolean = false,
    val sensorStripTypeError : Boolean = false,
    val sensorHighLowDetection : Boolean = false,
    val sensorTemperatureHighLowDetection : Boolean = false,
    val sensorReadInterruptedDetection : Boolean = false,
    val generalDeviceFaultDetection : Boolean = false,
    val timeFault : Boolean = false,
    val multipleBonds : Boolean = false
) : DataRecord()

//These are all UTF8 strings
sealed class DeviceInfoComponent : DataRecord() {
    class ModelNumber(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
    class SerialNumber(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
    class FirmwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
    class HardwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
    class SoftwareRevision(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
    class ManufacturerName(
        value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent()
}

sealed class DeviceInfo (
    val modelNumber : String = "",
    val serialNumber : String = "",
    val firmwareRevision: String = "",
    val hardwareRevision : String = "",
    val softwareRevision : String = "",
    val manufacturerName: String = ""
) :  DataRecord()

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

data class GlucoseRecordContext(
    val sequenceNumber : UInt,
    val carbohydrateType: CarbohydrateType?,
    val mealWeightKg : Float?,//int KG
    val mealContext: MealContext?,
    val tester : Tester?,
    val health : Health?,
    val exerciseDuration : UInt?,
    val exerciseIntensityPercent : UInt?,
    val medicationID: MedicationID?,
    val medicationInKg : Float?,
    val medicationInLiter : Float?,
    val HbA1cPercent : Float?
) : DataRecord() {
    companion object {
        fun fromISOValues(
            sequenceNumber: ISOValue.UInt16,
            carbohydrateType: ISOValue.UInt8?,
            mealWeight: ISOValue.SFloat?,
            mealContext: ISOValue.UInt8?,
            tester: ISOValue.UInt8?,
            health: ISOValue.UInt8?,
            exerciseDuration: ISOValue.UInt16?,
            exerciseIntensityPercent: ISOValue.UInt8?,
            medicationID: ISOValue.UInt8?,
            medicationInKg: ISOValue.SFloat?,
            medicationInLiter: ISOValue.SFloat?,
            HbA1cPercent: ISOValue.SFloat?
        ) : GlucoseRecordContext? {
            if (mealWeight !is ISOValue.SFloat.Value) return null
            if (medicationInKg !is ISOValue.SFloat.Value) return null
            if (medicationInLiter !is ISOValue.SFloat.Value) return null
            if (HbA1cPercent !is ISOValue.SFloat.Value) return null

            return GlucoseRecordContext(
                sequenceNumber.value,
                CarbohydrateType.fromUInt(carbohydrateType?.value),
                mealWeight.value,
                MealContext.fromUint(mealContext?.value),
                Tester.fromUint(tester?.value),
                Health.fromUInt(health?.value),
                exerciseDuration?.value,
                exerciseIntensityPercent?.value,
                MedicationID.fromUInt(medicationID?.value),
                medicationInKg.value,
                medicationInLiter.value,
                HbA1cPercent.value
            )
        }
    }
}

sealed class CarbohydrateType(val value : UInt) {
    object ReservedForFutureUse : CarbohydrateType(0u)
    object Breakfast : CarbohydrateType(1u)
    object Lunch : CarbohydrateType(2u)
    object Dinner : CarbohydrateType(3u)
    object Snack : CarbohydrateType(4u)
    object Drink : CarbohydrateType(5u)
    object Supper : CarbohydrateType(6u)
    object Brunch : CarbohydrateType(7u)

    companion object {
        fun fromUInt(i : UInt?) = when(i) {
            null -> null
            1u -> Breakfast
            2u -> Lunch
            3u -> Dinner
            4u -> Snack
            5u -> Drink
            6u -> Supper
            7u -> Brunch
            else -> ReservedForFutureUse
        }
    }
}

sealed class MealContext(val value : UInt) {
    object ReservedForFutureUse : MealContext(0u)
    object Preprandial : MealContext(1u)
    object Postprandial : MealContext(2u)
    object Fasting : MealContext(3u)
    object Casual : MealContext(4u) // casual drink / snack etc.
    object Bedtime : MealContext(5u)

    companion object{
        fun fromUint(i : UInt?) = when(i) {
            null -> null
            1u -> Preprandial
            2u -> Postprandial
            3u -> Fasting
            4u -> Casual
            5u -> Bedtime
            else -> ReservedForFutureUse
        }
    }
}

sealed class Tester(val value : UInt) {
    object ReservedForFutureUse : Tester(0u)
    object Self : Tester(1u)
    object HealthCareProfessional : Tester(2u)
    object LabTest : Tester(3u)
    object NotAvailable : Tester(15u)

    companion object {
        fun fromUint(i : UInt?) = when(i) {
            null -> null
            1u -> Self
            2u -> HealthCareProfessional
            3u -> LabTest
            15u -> NotAvailable
            else -> ReservedForFutureUse
        }
    }
}

sealed class Health(val value : UInt) {
    object ReservedForFutureUse : Health(0u)
    object MinorHealthIssues : Health(1u)
    object MajorHealthIssues : Health(2u)
    object DuringMenses : Health(3u)
    object UnderStress : Health(4u)
    object NoHealthIssues : Health(5u)
    object NotAvailable : Health(15u)

    companion object {
        fun fromUInt(i : UInt?) = when(i) {
            null -> null
            1u -> MinorHealthIssues
            2u -> MajorHealthIssues
            3u -> DuringMenses
            4u -> UnderStress
            5u -> NoHealthIssues
            15u -> NotAvailable
            else -> ReservedForFutureUse
        }
    }
}

sealed class MedicationID(val value : UInt) {
    object ReservedForFutureUse : MedicationID(0u)
    object RapidActingInsulin : MedicationID(1u)
    object ShortActingInsuling : MedicationID(2u)
    object IntermediateActingInsulin : MedicationID(3u)
    object LongActingInsulin : MedicationID(4u)
    object PremixedInsulin : MedicationID(5u)

    companion object{
        fun fromUInt(i : UInt?) = when(i) {
            null -> null
            1u -> RapidActingInsulin
            2u -> ShortActingInsuling
            3u -> IntermediateActingInsulin
            4u -> LongActingInsulin
            5u -> PremixedInsulin
            else -> ReservedForFutureUse
        }
    }
}

