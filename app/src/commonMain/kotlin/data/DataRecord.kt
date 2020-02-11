package data

import iso.ISOValue
import sample.logger
import util.strRepresentation
import util.toUnsignedInt


sealed class DataRecord(val device: PeripheralDescription)

class EmptyRecord(device : PeripheralDescription) : DataRecord(device)

class PulseOximeterFeatures(
    measurementStatus: Boolean,
    deviceAndSensorStatus: Boolean,
    measurementStoreForSpotCheck : Boolean,
    timestampForSpotCheck: Boolean,
    spo2PRFast : Boolean,
    spo2PRSlow : Boolean,
    pulseAmplitudeIndexField: Boolean,
    multipleBonds: Boolean,
    device: PeripheralDescription
): DataRecord(device)


class PLXSpotCheck(
    spo2: ISOValue.SFloat,
    PR: ISOValue.SFloat,
    timeStamp: ISOValue.DateTime?,
    measurementStatus: PLXSpotCheckMeasurementStatus?,
    sensorStatus: PLXSpotCheckSensorStatus?,
    pulseAmplitudeIndex : ISOValue.SFloat?,
    device: PeripheralDescription
): DataRecord(device) {
    companion object{
        fun fromISO(
            spo2: ISOValue.SFloat,
            PR: ISOValue.SFloat,
            timeStamp: ISOValue.DateTime?,
            measurementStatus: ISOValue.SInt16?,
            sensorstatus1: ISOValue.SInt16?,
            sensorstatus2: ISOValue.SInt8?,
            pulseAmplitudeIndex : ISOValue.SFloat?,
            device : PeripheralDescription
        ) : PLXSpotCheck? {
            val measureStatusEnum = if(measurementStatus != null) PLXSpotCheckMeasurementStatus.fromInt(measurementStatus.value) else null
            val sensorStatus = if(sensorstatus1 != null && sensorstatus2 != null)
                PLXSpotCheckSensorStatus.fromInt(sensorstatus1.value or sensorstatus2.value.shl(16))
                else null

            return PLXSpotCheck(
                spo2,
                PR,
                timeStamp,
                measureStatusEnum,
                sensorStatus,
                pulseAmplitudeIndex,
                device
            )
        }
    }
}


class PLXContinousMeasurement(
    spo2Normal: ISOValue.SFloat,
    PRNormal: ISOValue.SFloat,
    spo2Fast: ISOValue.SFloat?,
    PRFast: ISOValue.SFloat?,
    spo2Slow: ISOValue.SFloat?,
    PRSlow: ISOValue.SFloat?,
    measurementStatus: PLXSpotCheckMeasurementStatus?,
    sensorStatus: PLXSpotCheckSensorStatus?,
    pulseAmplitudeIndex : ISOValue.SFloat?,
    device: PeripheralDescription
): DataRecord(device) {
    companion object{
        fun fromISO(
            spo2Normal: ISOValue.SFloat,
            PRNormal: ISOValue.SFloat,
            spo2Fast: ISOValue.SFloat?,
            PRFast: ISOValue.SFloat?,
            spo2Slow: ISOValue.SFloat?,
            PRSlow: ISOValue.SFloat?,
            measurementStatus: ISOValue.SInt16?,
            sensorstatus1: ISOValue.SInt16?,
            sensorstatus2: ISOValue.SInt8?,
            pulseAmplitudeIndex : ISOValue.SFloat?,
            device: PeripheralDescription
        ) : PLXContinousMeasurement? {
            val measureStatusEnum = if(measurementStatus != null) PLXSpotCheckMeasurementStatus.fromInt(measurementStatus.value) else null
            val sensorStatus = if(sensorstatus1 != null && sensorstatus2 != null)
                PLXSpotCheckSensorStatus.fromInt(sensorstatus1.value or sensorstatus2.value.shl(16))
            else null

            return PLXContinousMeasurement(
                spo2Normal,
                PRNormal,
                spo2Fast,
                PRFast,
                spo2Slow,
                PRSlow,
                measureStatusEnum,
                sensorStatus,
                pulseAmplitudeIndex,
                device
            )
        }
    }
}

enum class PLXSpotCheckSensorStatus {
    ExtendedDisplayUpdateOngoing,
    EquipmentMalfunctionDetected,
    SignalProcessingIrregularityDetected,
    InadequateSignalDetected,
    PoorSignalDetected,
    LowPerfusionDetected,
    ErraticSignalDetected,
    NonPulsatileSignalDetected,
    QuestionablePulseDetected,
    SignalAnalysisOngoing,
    SensorInterfaceDetected,
    SensorUnconnectedToUser,
    UnknownSensorConnected,
    SensorDisplaced,
    SensorMalfunction,
    SensorDisconnected,
    ReservedForFutureUse;

    companion object {
        fun fromInt(i: Int) = when (i) {
            0 -> ExtendedDisplayUpdateOngoing
            1 -> EquipmentMalfunctionDetected
            2 -> SignalProcessingIrregularityDetected
            3 -> InadequateSignalDetected
            4 -> PoorSignalDetected
            5 -> LowPerfusionDetected
            6 -> ErraticSignalDetected
            7 -> NonPulsatileSignalDetected
            8 -> QuestionablePulseDetected
            9 -> SignalAnalysisOngoing
            10 -> SensorInterfaceDetected
            11 -> SensorUnconnectedToUser
            12 -> UnknownSensorConnected
            13 -> SensorDisplaced
            14 -> SensorMalfunction
            15 -> SensorDisconnected
            else -> ReservedForFutureUse
        }
    }
}

enum class PLXSpotCheckMeasurementStatus {
    MeasurementOngoing,
    EarlyEstimateData,
    ValidatedData,
    FullyQualifiedData,
    DataFromMeasurementStorage,
    DataForDemonstration,
    DataForTesting,
    CalibrationOngoing,
    MeasurementUnavailable,
    QuestionableMeasurementDetected,
    InvalidMeasurementDetecteds,
    ReservedForFutureUse;

    companion object {
            fun fromInt(i : Int) : PLXSpotCheckMeasurementStatus = when(i) {
            5 -> MeasurementOngoing
            6 -> EarlyEstimateData
            7-> ValidatedData
            8 -> FullyQualifiedData
            9 -> DataFromMeasurementStorage
            10 -> DataForDemonstration
            11 -> DataForTesting
            12 -> CalibrationOngoing
            13 -> MeasurementUnavailable
            14 -> QuestionableMeasurementDetected
            15 -> InvalidMeasurementDetecteds
            else -> ReservedForFutureUse
        }
    }
}
class ThermometerMeasurement(
    val measurementValue: ISOValue.Float,
    val measurementUnit: TemperatureUnit,
    val timeStamp: ISOValue.DateTime?,
    val temperatureType: TemperatureType?,
    device: PeripheralDescription
): DataRecord(device)

sealed class TemperatureType {
    object Armpit: TemperatureType()
    object Body : TemperatureType()
    object Ear: TemperatureType()
    object Finger: TemperatureType()
    object GastroIntestinalTract: TemperatureType()
    object Mouth: TemperatureType()
    object Rectum: TemperatureType()
    object Toe: TemperatureType()
    object Tympanum : TemperatureType()
    object ReservedForFutureUse : TemperatureType()

    companion object {
        fun fromInt(value: Int) = when(value) {
            1 -> Armpit
            2 -> Body
            3 -> Ear
            4 -> Finger
            5 -> GastroIntestinalTract
            6 -> Mouth
            7 -> Rectum
            8 -> Toe
            9 -> Tympanum
            else -> ReservedForFutureUse
        }
    }
}

//These are all UTF8 strings
sealed class DeviceInfoComponent(device: PeripheralDescription) : DataRecord(device) {
    class ModelNumber(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
    class SerialNumber(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
    class FirmwareRevision(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
    class HardwareRevision(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
    class SoftwareRevision(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
    class ManufacturerName(
        val value : String,
        device : PeripheralDescription
    ) : DeviceInfoComponent(device)
}

class DeviceInfoRecord (
    val modelNumber : String = "",
    val serialNumber : String = "",
    val firmwareRevision: String = "",
    val hardwareRevision : String = "",
    val softwareRevision : String = "",
    val manufacturerName: String = "",
    device: PeripheralDescription
) :  DataRecord(device) {
    override fun toString(): String {
        return """
            DeviceInfoRecord(
            modelNr: $modelNumber,
            serialNr: $serialNumber,
            manufacturer: $manufacturerName,
            )
        """.trimIndent()
    }
}

class BatteryLevelRecord(
    level : Int,
    device : PeripheralDescription
) : DataRecord(device) {
    constructor(level: ISOValue.UInt8, device: PeripheralDescription) : this(level.value.toInt(), device)
}

class BloodPressureFeatures(
    val bodyMovementDetection : Boolean,
    val cuffFitDetection : Boolean,
    val irregularPulseDetection : Boolean,
    val pulseRateRangeDetection : Boolean,
    val measurementPositionDetection : Boolean,
    val multipleBondSupport : Boolean,
    device: PeripheralDescription
) : DataRecord(device)

class BloodPressureRecord(
    val systolic : ISOValue.SFloat,
    val diastolic : ISOValue.SFloat,
    val meanArtieralPressure : ISOValue.SFloat,
    val unit : BloodPressureUnit,
    val timeStamp : ISOValue.DateTime?,
    val bpm : ISOValue.SFloat?,
    val userId : Int?,
    val status : MeasurementStatus?,
    device: PeripheralDescription
) : DataRecord(device){

    companion object {
        fun fromISOValues(
            systolic: ISOValue.SFloat,
            diastolic: ISOValue.SFloat,
            meanArtieralPressure: ISOValue.SFloat,
            unit: BloodPressureUnit,
            timeStamp: ISOValue.DateTime?,
            bpm: ISOValue.SFloat?,
            userId: ISOValue.UInt8?,
            status: MeasurementStatus?,
            device: PeripheralDescription
        ): BloodPressureRecord? {
            if (systolic !is ISOValue.SFloat.Value) return null
            if (diastolic !is ISOValue.SFloat.Value) return null
            if (meanArtieralPressure !is ISOValue.SFloat.Value) return null
            if (bpm != null && bpm !is ISOValue.SFloat.Value) return null

            return BloodPressureRecord(
                systolic,
                diastolic,
                meanArtieralPressure,
                unit,
                timeStamp,
                bpm,
                userId?.value,
                status,
                device
            )
        }
    }
}

class MeasurementStatus()

class BodyCompositionFeature(
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
    val heightMeasurementResolution: HeightMeasurementResolution,
    device: PeripheralDescription
) : DataRecord(device)

class BodyCompositionRecord(
   val bodyFatPercent : Int,
   val dateTime: ISOValue.DateTime?,
   val userId: Int?,
   val basalMetabolism : Int?,
   val musclePercent : Int?,
   val muscleMass : Int?,
   val fatFreeMass : Int?,
   val softLeanMass : Int?,
   val bodyWaterMass : Int?,
   val impedance: Int?,
   device: PeripheralDescription,
   val heightMeasurementResolution: HeightMeasurementResolution = HeightMeasurementResolution.HighRes,
   val weightMeasurementResolution: WeightMeasurementResolution = WeightMeasurementResolution.Res7
   //val weight : UInt,
   //val height : UInt
) : DataRecord(device) {
    constructor(bodyFatPercent : ISOValue.UInt16,
                dateTime: ISOValue.DateTime?,
                userId: ISOValue.UInt8?,
                basalMetabolism : ISOValue.UInt16?,
                musclePercent : ISOValue.UInt16?,
                muscleMass : ISOValue.UInt16?,
                fatFreeMass : ISOValue.UInt16?,
                softLeanMass : ISOValue.UInt16?,
                bodyWaterMass : ISOValue.UInt16?,
                impedance: ISOValue.UInt16?,
                device: PeripheralDescription)
            : this(bodyFatPercent.value,
                dateTime,
                userId?.value,
                basalMetabolism?.value,
                musclePercent?.value,
                muscleMass?.value,
                fatFreeMass?.value,
                softLeanMass?.value,
                bodyWaterMass?.value,
                impedance?.value,
                device)
}



class WeightRecord(
    val weight: Int,
    val weightUnit: WeightUnit,
    val timestamp : ISOValue.DateTime?,
    val userId: Int?,
    val BMI : Int?,
    val height: Int?,
    val heightUnit: LengthUnit?,
    device: PeripheralDescription,
    val heightMeasurementResolution: HeightMeasurementResolution = HeightMeasurementResolution.HighRes,
    val weightMeasurementResolution: WeightMeasurementResolution = WeightMeasurementResolution.Res7
) : DataRecord(device) {
    companion object{
        fun fromISOValues(
            weight : ISOValue.UInt16,
            weightUnit: WeightUnit,
            timeStamp: ISOValue.DateTime?,
            userId: ISOValue.UInt8?,
            BMI: ISOValue.UInt16?,
            height: ISOValue.UInt16?,
            heightUnit: LengthUnit?,
            device: PeripheralDescription
        ) : WeightRecord? {
            if(height != null && heightUnit == null) return null
            return WeightRecord(
                weight.value,
                weightUnit,
                timeStamp,
                userId?.value,
                BMI?.value,
                height?.value,
                heightUnit,
                device
            )
        }
    }
}

class WeightFeatures(
    val timeStamp : Boolean,
    val multipleUsers : Boolean,
    val BMI : Boolean,
    val weightMeasurementResolution: WeightMeasurementResolution,
    val heightMeasurementResolution : HeightMeasurementResolution,
    device: PeripheralDescription
) : DataRecord(device)

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

class HeartRateRecord(
    val measurementValue : Int,
    val energyExpended : Int?,
    val sensorContact : SensorContact,
    val rrInterval : Int?,
    device: PeripheralDescription
) : DataRecord(device) {
    constructor(
        measurementValue: ISOValue.UInt8,
        energyExpended: ISOValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: ISOValue.UInt16?,
        device: PeripheralDescription
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.value,device)

    constructor(
        measurementValue: ISOValue.UInt16,
        energyExpended: ISOValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: ISOValue.UInt16?,
        device: PeripheralDescription
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.value,device)
}

sealed class SensorContact {
    object NotSupported : SensorContact()
    object ContactNotDetected : SensorContact()
    object ContactDetected : SensorContact()
}

class BodySensorLocationRecord(
    val location : BodySensorLocation,
    device: PeripheralDescription
) : DataRecord(device)

sealed class BodySensorLocation {
    object Other : BodySensorLocation()
    object Chest : BodySensorLocation()
    object Wrist : BodySensorLocation()
    object Finger : BodySensorLocation()
    object Hand : BodySensorLocation()
    object EarLobe : BodySensorLocation()
    object Foot : BodySensorLocation()
}

sealed class BloodSampleType {
    object ReservedForFutureUse : BloodSampleType()
    object CapillaryWholeBlood : BloodSampleType()
    object CapillaryPlasma : BloodSampleType()
    object VenousWholeBlood : BloodSampleType()
    object VenousPlasma : BloodSampleType()
    object ArterialWholeBlood : BloodSampleType()
    object ArterialPlasma : BloodSampleType()
    object UndeterminedWholeBlood : BloodSampleType()
    object UndeterminedPlasma: BloodSampleType()
    object InterstitialFluid : BloodSampleType()
    object ControlSolution : BloodSampleType()

    companion object {
        fun fromInt(i : Int) = when(i) {
            1 -> CapillaryWholeBlood
            2 -> CapillaryPlasma
            3-> VenousWholeBlood
            4 -> VenousPlasma
            5 -> ArterialWholeBlood
            6 -> ArterialPlasma
            7 -> UndeterminedWholeBlood
            8 -> UndeterminedPlasma
            9 -> InterstitialFluid
            10 -> ControlSolution
            else -> ReservedForFutureUse
        }
    }
}

class ControlPointRecord(val response : RecordControlPointResponse, device: PeripheralDescription) : DataRecord(device) {
    override fun toString(): String {
        return response.toString()
    }
}

enum class RecordControlPointResponse {
    ReservedforFutureUse,
    Success,
    OPCodenotSupported,
    InvalidOperator,
    OperatorNotSupported,
    InvalidOperand,
    NoRecordsFound,
    AbortUnsucessful,
    ProcedureNotCompleted,
    OperandNotSupported;

    companion object{
        fun fromInt(i : Int) = when(i) {
            1 -> Success
            2 -> OPCodenotSupported
            3 -> InvalidOperator
            4 -> OperatorNotSupported
            5 -> InvalidOperand
            6 -> NoRecordsFound
            7 -> AbortUnsucessful
            8 -> ProcedureNotCompleted
            9 -> OperandNotSupported
            else -> ReservedforFutureUse
        }
    }
}

enum class GlucoseSensorStatusAnunciation {
    BatteryLowAtMeasurement,
    SensorMalfunctionAtMeasurement,
    SampleSizeOfBloodInsufficient,
    StripInsertionError,
    StripTypeIncorrect,
    SensorResultTooHigh,
    SensorResultTooLow,
    SensorTemperatureTooHigh,
    SensorTemperatureTooLow,
    SensorReadInterrupted,
    GeneralDeviceFault,
    TimeFault_TimeMightBeInaccurate,
    ReservedForFutureUse;

    companion object {
        fun fromInt(i : Int) = when(i) {
            0 -> BatteryLowAtMeasurement
            1 -> SensorMalfunctionAtMeasurement
            2 -> SampleSizeOfBloodInsufficient
            3 -> StripInsertionError
            4 -> StripTypeIncorrect
            5 -> SensorResultTooHigh
            6 -> SensorResultTooLow
            7 -> SensorTemperatureTooHigh
            8 -> SensorTemperatureTooLow
            9 -> SensorReadInterrupted
            10 -> GeneralDeviceFault
            11 -> TimeFault_TimeMightBeInaccurate
            else -> ReservedForFutureUse

        }
    }
}

sealed class GlucoseSampleLocation {
    object ReservedForFutureUse : GlucoseSampleLocation()
    object Finger : GlucoseSampleLocation()
    object AlternateTestSite: GlucoseSampleLocation()
    object Earlobe : GlucoseSampleLocation()
    object ControlSolution: GlucoseSampleLocation()
    object SampleLocationNotAvailable : GlucoseSampleLocation()

    companion object {
        fun fromInt(i : Int) = when(i) {
            1 -> Finger
            2 -> AlternateTestSite
            3 -> Earlobe
            4 -> ControlSolution
            15 -> SampleLocationNotAvailable
            else -> ReservedForFutureUse
        }
    }
}

class GlucoseRecord(
    val unit : BloodGlucoseUnit,
    val amount : ISOValue.SFloat,
    val sequenceNumber : Int,
    val baseTime : ISOValue.DateTime,
    val timeOffset: Int?,
    val sampleType: BloodSampleType?,
    val sampleLocation: GlucoseSampleLocation?,
    val sensorStatusAnunciation: GlucoseSensorStatusAnunciation?,
    val context : HasGlucoseContext,
    device: PeripheralDescription
) : DataRecord(device) {
    fun copyWithContext(ctx : HasGlucoseContext) = GlucoseRecord(
        unit,
        amount,
        sequenceNumber,
        baseTime,
        timeOffset,
        sampleType,
        sampleLocation,
        sensorStatusAnunciation,
        ctx,
        device
    )

    override fun toString(): String = """
    Glucose Record ( 
    unit: $unit,
    amount: $amount,
    sequenceNr: $sequenceNumber,
    baseTime: $baseTime,
    timeOffset: $timeOffset
    context: $context
    sampleType: $sampleType
    sampleLocation $sampleLocation
    sensorStatusAnunciation: $sensorStatusAnunciation
    )
    """.trimIndent()

    companion object {
        fun fromISOValues(
            unit : ISOValue.Flag,
            amount : ISOValue.SFloat?,
            sequenceNumber : ISOValue.UInt16,
            baseTime: ISOValue.DateTime,
            timeOffset: ISOValue.SInt16?,
            contextFollows : Boolean,
            sampleType: ISOValue.Nibble?,
            sampleLocation: ISOValue.Nibble?,
            sensorStatusAnunciation: ISOValue.UInt16?,
            device: PeripheralDescription
        ) : GlucoseRecord? {
            return GlucoseRecord(
                if(unit.value) BloodGlucoseUnit.MMOL else BloodGlucoseUnit.DL,
                amount ?: return null,
                sequenceNumber.value,
                baseTime,
                timeOffset?.value,
                if(sampleType!=null) BloodSampleType.fromInt(sampleType.unsigned) else null,
                if(sampleLocation!=null) GlucoseSampleLocation.fromInt(sampleLocation.unsigned) else null,
                if(sensorStatusAnunciation!=null) GlucoseSensorStatusAnunciation.fromInt(sensorStatusAnunciation.value) else null,
                if (contextFollows) HasGlucoseContext.NotReceivedYet else HasGlucoseContext.NotSupported,
                device
            )
        }
    }
}

sealed class HasGlucoseContext {
    object NotReceivedYet : HasGlucoseContext()
    object NotSupported : HasGlucoseContext()
    class Context(val value : GlucoseRecordContext) : HasGlucoseContext() {
        override fun toString(): String {
            return value.toString()
        }
    }
}

//describes a glucse peripherals supported flags
class GlucoseFeatures (
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
    val multipleBonds : Boolean = false,
    device: PeripheralDescription
) : DataRecord(device) {
    override fun toString(): String = """ 
        GlucoseFeatures(
        lowBattery: $lowBattery,
        sensorMalfunction: $sensorMalfunction,
        sensorSampleSize. $sensorSampleSize,
        ensorStripInsertionMalfunction: $sensorStripInsertionMalfunction,
        sensorStripTypeError: $sensorStripTypeError,
        sensorHighLowDetection: $sensorHighLowDetection,
        sensorTempHighLow: $sensorTemperatureHighLowDetection,
        ssensorReadInterrupt: $sensorReadInterruptedDetection,
        generalDevicefault: $generalDeviceFaultDetection,
        timeFault: $timeFault,
        multipleBonds: $multipleBonds,
        )
        """
}

class GlucoseRecordContext(
    val sequenceNumber : Int,
    val carbohydrateType: CarbohydrateType?,
    val mealWeightKg : ISOValue.SFloat?,//int KG
    val mealContext: MealContext?,
    val tester : Tester?,
    val health : Health?,
    val exerciseDuration : Int?,
    val exerciseIntensityPercent : Int?,
    val medicationID: MedicationID?,
    val medicationInKg : ISOValue.SFloat?,
    val medicationInLiter : ISOValue.SFloat?,
    val HbA1cPercent : ISOValue.SFloat?,
    device: PeripheralDescription
) : DataRecord(device) {

    override fun toString(): String = """
        GlucoseRecordContext(
        sequenceNumber: $sequenceNumber,
        carbohydrateType: $carbohydrateType,
        mealWeightKg: $mealWeightKg,
        mealContext: $mealContext,
        tester: $tester,
        health: $health,
        exerciseDuration: $exerciseDuration,
        exerciseIntensityPercent: $exerciseIntensityPercent,
        medicationID: $medicationID,
        medicationInKg: $medicationInKg,
        medicationInLiter: $medicationInLiter,
        HbA1cPercent: $HbA1cPercent,
        )
    """.trimIndent()
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
            HbA1cPercent: ISOValue.SFloat?,
            device: PeripheralDescription
        ) : GlucoseRecordContext? {
            return GlucoseRecordContext(
                sequenceNumber.value,
                CarbohydrateType.fromUInt(carbohydrateType?.value),
                mealWeight,
                MealContext.fromUint(mealContext?.value),
                Tester.fromUint(tester?.value),
                Health.fromUInt(health?.value),
                exerciseDuration?.value,
                exerciseIntensityPercent?.value,
                MedicationID.fromUInt(medicationID?.value),
                medicationInKg,
                medicationInLiter,
                HbA1cPercent,
                device
            )
        }
    }
}

sealed class CarbohydrateType(val value : Int) {
    object ReservedForFutureUse : CarbohydrateType(0)
    object Breakfast : CarbohydrateType(1)
    object Lunch : CarbohydrateType(2)
    object Dinner : CarbohydrateType(3)
    object Snack : CarbohydrateType(4)
    object Drink : CarbohydrateType(5)
    object Supper : CarbohydrateType(6)
    object Brunch : CarbohydrateType(7)

    companion object {
        fun fromUInt(i : Int?) = when(i) {
            null -> null
            1 -> Breakfast
            2 -> Lunch
            3 -> Dinner
            4 -> Snack
            5 -> Drink
            6 -> Supper
            7 -> Brunch
            else -> ReservedForFutureUse
        }
    }
}

sealed class MealContext(val value : Int) {
    object ReservedForFutureUse : MealContext(0)
    object Preprandial : MealContext(1)
    object Postprandial : MealContext(2)
    object Fasting : MealContext(3)
    object Casual : MealContext(4) // casual drink / snack etc.
    object Bedtime : MealContext(5)

    companion object{
        fun fromUint(i : Int?) = when(i) {
            null -> null
            1 -> Preprandial
            2 -> Postprandial
            3 -> Fasting
            4 -> Casual
            5 -> Bedtime
            else -> ReservedForFutureUse
        }
    }
}

sealed class Tester(val value : Int) {
    object ReservedForFutureUse : Tester(0)
    object Self : Tester(1)
    object HealthCareProfessional : Tester(2)
    object LabTest : Tester(3)
    object NotAvailable : Tester(15)

    companion object {
        fun fromUint(i : Int?) = when(i) {
            null -> null
            1 -> Self
            2 -> HealthCareProfessional
            3 -> LabTest
            15 -> NotAvailable
            else -> ReservedForFutureUse
        }
    }
}

sealed class Health(val value : Int) {
    object ReservedForFutureUse : Health(0)
    object MinorHealthIssues : Health(1)
    object MajorHealthIssues : Health(2)
    object DuringMenses : Health(3)
    object UnderStress : Health(4)
    object NoHealthIssues : Health(5)
    object NotAvailable : Health(15)

    companion object {
        fun fromUInt(i : Int?) = when(i) {
            null -> null
            1 -> MinorHealthIssues
            2 -> MajorHealthIssues
            3 -> DuringMenses
            4 -> UnderStress
            5 -> NoHealthIssues
            15 -> NotAvailable
            else -> ReservedForFutureUse
        }
    }
}

sealed class MedicationID(val value : Int) {
    object ReservedForFutureUse : MedicationID(0)
    object RapidActingInsulin : MedicationID(1)
    object ShortActingInsuling : MedicationID(2)
    object IntermediateActingInsulin : MedicationID(3)
    object LongActingInsulin : MedicationID(4)
    object PremixedInsulin : MedicationID(5)

    companion object{
        fun fromUInt(i : Int?) = when(i) {
            null -> null
            1 -> RapidActingInsulin
            2 -> ShortActingInsuling
            3 -> IntermediateActingInsulin
            4 -> LongActingInsulin
            5 -> PremixedInsulin
            else -> ReservedForFutureUse
        }
    }
}