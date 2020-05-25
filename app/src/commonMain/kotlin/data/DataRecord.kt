package data

import bledata.CurrentTime
import bledata.PeripheralDescription
import data.glucose.*
import data.pulseoximeter.*
import gatt.GATTValue

sealed class DataRecord(val device: PeripheralDescription) {
    override fun toString(): String = this::class.simpleName ?: "DataRecord"
}

class EmptyRecord(device : PeripheralDescription) : DataRecord(device)

class DateTimeRecord(
    val dateTime: GATTValue.DateTime,
    device: PeripheralDescription
) : DataRecord(device){
    override fun toString(): String {
        return dateTime.toString()
    }
}

class CurrentTimeRecord(
    val currentTime: CurrentTime,
    device: PeripheralDescription
) : DataRecord(device){

    override fun toString(): String {
        return currentTime.toString()
    }

}

class PulseOximeterFeatures(
    val supportedFeatures: PulseOximeterSupportedFeatures,
    val measurementStatusSupport: PulseOximeterMeasurementStatusSupport?,
    val deviceAndSensorStatusSupport: PulseOximeterDeviceAndSensorStatusSupport?,
    device: PeripheralDescription
): DataRecord(device) {
    override fun toString(): String {
        return """PulseOximeterFeatures(
            supportedFeatures: $supportedFeatures,
            measurementStatusSupport: ${measurementStatusSupport ?: "not supported"},
            deviceAndSensorStatusSupport: ${deviceAndSensorStatusSupport ?: "not supported"}
            )
        """.trimIndent()
    }
}

class PLXSpotCheck(
    val spo2: GATTValue.SFloat,
    val PR: GATTValue.SFloat,
    val timeStamp: GATTValue.DateTime?,
    val measurementStatus: PLXSpotCheckMeasurementStatus?,
    val sensorStatus: PLXSpotCheckSensorStatus?,
    val pulseAmplitudeIndex : GATTValue.SFloat?,
    device : PeripheralDescription
): DataRecord(device) {
    override fun toString(): String {
        return """
            PLXSpotCheck(
                spo2: $spo2,
                PR: $PR,
                timeStamp: $timeStamp,
                measurementStatus: $measurementStatus,
                sensorStatus: $sensorStatus,
                pulseAmplitudeIndex: $pulseAmplitudeIndex
            )
        """.trimIndent()
    }
    companion object{
        fun fromISO(
            spo2: GATTValue.SFloat,
            PR: GATTValue.SFloat,
            timeStamp: GATTValue.DateTime?,
            measurementStatus: GATTValue.SInt16?,
            sensorstatus1: GATTValue.SInt16?,
            sensorstatus2: GATTValue.SInt8?,
            pulseAmplitudeIndex : GATTValue.SFloat?,
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
    val spo2Normal: GATTValue.SFloat,
    val PRNormal: GATTValue.SFloat,
    val spo2Fast: GATTValue.SFloat?,
    val PRFast: GATTValue.SFloat?,
    val spo2Slow: GATTValue.SFloat?,
    val PRSlow: GATTValue.SFloat?,
    val measurementStatus: PLXSpotCheckMeasurementStatus?,
    val sensorStatus: PLXSpotCheckSensorStatus?,
    val pulseAmplitudeIndex : GATTValue.SFloat?,
    device: PeripheralDescription
): DataRecord(device) {
    companion object{
        fun fromISO(
            spo2Normal: GATTValue.SFloat,
            PRNormal: GATTValue.SFloat,
            spo2Fast: GATTValue.SFloat?,
            PRFast: GATTValue.SFloat?,
            spo2Slow: GATTValue.SFloat?,
            PRSlow: GATTValue.SFloat?,
            measurementStatus: GATTValue.SInt16?,
            sensorstatus1: GATTValue.SInt16?,
            sensorstatus2: GATTValue.SInt8?,
            pulseAmplitudeIndex : GATTValue.SFloat?,
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

class ThermometerMeasurement(
    val measurementValue: GATTValue.Float,
    val measurementUnit: TemperatureUnit,
    val timeStamp: GATTValue.DateTime?,
    val temperatureType: TemperatureType?,
    device: PeripheralDescription
): DataRecord(device) {
    override fun toString(): String = """
        ThermometerMeasurement(
            measurementValue: $measurementValue,
            measurementUnit: $measurementUnit,
            timestamp: $timeStamp,
            type: $temperatureType
        )
    """.trimIndent()
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
                firmwareRevision: $firmwareRevision,
                hardwareRevision: $hardwareRevision,
                softwareRevision: $softwareRevision,
                manufacturer: $manufacturerName,
            )
        """.trimIndent()
    }
}

class BatteryLevelRecord(
    val level : Int,
    device: PeripheralDescription
) : DataRecord(device) {
    override fun toString(): String {
        return "BatteryLevel: $level"
    }
    constructor(level: GATTValue.UInt8, device: PeripheralDescription) : this(level.value, device)
}

class BloodPressureFeatures(
    val bodyMovementDetection : Boolean,
    val cuffFitDetection : Boolean,
    val irregularPulseDetection : Boolean,
    val pulseRateRangeDetection : Boolean,
    val measurementPositionDetection : Boolean,
    val multipleBondSupport : Boolean,
    device: PeripheralDescription
) : DataRecord(device) {
    override fun toString(): String {
        return """BloodPressureFeatures: (
                bodyMovementDetection: $bodyMovementDetection,
                cuffFitDetection: $cuffFitDetection,
                irregularPulseDetection: $irregularPulseDetection,
                pulseRateRangeDetection: $pulseRateRangeDetection,
                measurementPositionDetection: $measurementPositionDetection,
                multipleBondSupport: $multipleBondSupport
            )
        """.trimIndent()
    }
}

sealed class BloodPressureRecord(
    val systolic : GATTValue.SFloat,
    val unit : BloodPressureUnit,
    val timeStamp : GATTValue.DateTime?,
    val bpm : GATTValue.SFloat?,
    val userId : Int?,
    val status : BPMeasurementStatus?,
    device : PeripheralDescription
) : DataRecord(device){


    override fun toString(): String {
        return """
            BloodPressureRecord: (
                systolic: $systolic,
                unit: $unit,
                timeStamp: $timeStamp,
                bpm: $bpm,
                userId: $userId,
                status: ${if(status != null)status::class.simpleName else ""}
            )
        """.trimIndent()
    }

    class IntermediateMeasurement(
        systolic : GATTValue.SFloat,
        unit : BloodPressureUnit,
        timeStamp : GATTValue.DateTime?,
        bpm : GATTValue.SFloat?,
        userId : Int?,
        status : BPMeasurementStatus?,
        device: PeripheralDescription
    ) : BloodPressureRecord(systolic,unit,timeStamp,bpm,userId,status,device) {
        override fun toString() = """
            IntermediateBloodPressureMeasurement: (
                systolic: $systolic,
                unit: $unit,
                timeStamp: $timeStamp,
                bpm: $bpm,
                userId: $userId,
                status: $status
            )
        """.trimIndent()
    }

    class FinalMeasurement(
        systolic : GATTValue.SFloat,
        val diastolic : GATTValue.SFloat,
        val meanArterialPressure : GATTValue.SFloat,
        unit : BloodPressureUnit,
        timeStamp : GATTValue.DateTime?,
        bpm : GATTValue.SFloat?,
        userId : Int?,
        status : BPMeasurementStatus?,
        device: PeripheralDescription
    ) : BloodPressureRecord(systolic,unit,timeStamp,bpm,userId,status,device) {
        override fun toString(): String {
            return """
            FinalBloodPressureMeasurement: (
                systolic: $systolic,
                diastolic: $diastolic,
                meanArterialPressure: $meanArterialPressure,
                unit: $unit,
                timeStamp: $timeStamp,
                bpm: $bpm,
                userId: $userId,
                status: $status
            )
        """.trimIndent()
        }
    }

    companion object {
        fun finalFromISO(
            systolic: GATTValue.SFloat,
            diastolic: GATTValue.SFloat,
            meanArtieralPressure: GATTValue.SFloat,
            unit: BloodPressureUnit,
            timeStamp: GATTValue.DateTime?,
            bpm: GATTValue.SFloat?,
            userId: GATTValue.UInt8?,
            status: BPMeasurementStatus?,
            device: PeripheralDescription
        ): BloodPressureRecord? {
            if (systolic !is GATTValue.SFloat.Value) return null
            if (diastolic !is GATTValue.SFloat.Value) return null
            if (meanArtieralPressure !is GATTValue.SFloat.Value) return null
            if (bpm != null && bpm !is GATTValue.SFloat.Value) return null

            return FinalMeasurement(
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
        fun intermediateFromISO(
            systolic: GATTValue.SFloat,
            unit: BloodPressureUnit,
            timeStamp: GATTValue.DateTime?,
            bpm: GATTValue.SFloat?,
            userId: GATTValue.UInt8?,
            status: BPMeasurementStatus?,
            device: PeripheralDescription
        ): IntermediateMeasurement? {
            if (systolic !is GATTValue.SFloat.Value) return null
            if (bpm != null && bpm !is GATTValue.SFloat.Value) return null

            return IntermediateMeasurement(
                systolic,
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
    val dateTime: GATTValue.DateTime?,
    val userId: Int?,
    val basalMetabolism : Int?,
    val musclePercent : Int?,
    val muscleMass : Int?,
    val fatFreeMass : Int?,
    val softLeanMass : Int?,
    val bodyWaterMass : Int?,
    val impedance: Int?,
    device: PeripheralDescription,
    val heightMeasurementResolution: HeightMeasurementResolution = HeightMeasurementResolution.NotSpecified,
    val weightMeasurementResolution: WeightMeasurementResolution = WeightMeasurementResolution.NotSpecified
   //val weight : UInt,
   //val height : UInt
) : DataRecord(device) {
    constructor(bodyFatPercent : GATTValue.UInt16,
                dateTime: GATTValue.DateTime?,
                userId: GATTValue.UInt8?,
                basalMetabolism : GATTValue.UInt16?,
                musclePercent : GATTValue.UInt16?,
                muscleMass : GATTValue.UInt16?,
                fatFreeMass : GATTValue.UInt16?,
                softLeanMass : GATTValue.UInt16?,
                bodyWaterMass : GATTValue.UInt16?,
                impedance: GATTValue.UInt16?,
                device: PeripheralDescription
    )
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
    val timestamp : GATTValue.DateTime?,
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
            weight : GATTValue.UInt16,
            weightUnit: WeightUnit,
            timeStamp: GATTValue.DateTime?,
            userId: GATTValue.UInt8?,
            BMI: GATTValue.UInt16?,
            height: GATTValue.UInt16?,
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


class HeartRateRecord(
    val measurementValue : Int,
    val energyExpended : Int?,
    val sensorContact : SensorContact,
    val rrInterval : List<Int>?,
    device: PeripheralDescription
) : DataRecord(device) {
    constructor(
        measurementValue: GATTValue.UInt8,
        energyExpended: GATTValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: List<GATTValue.UInt16>?,
        device: PeripheralDescription
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.map{it.value},device)

    constructor(
        measurementValue: GATTValue.UInt16,
        energyExpended: GATTValue.UInt16?,
        sensorContact: SensorContact,
        rrInterval: List<GATTValue.UInt16>?,
        device: PeripheralDescription
    ) : this(measurementValue.value,energyExpended?.value,sensorContact,rrInterval?.map{it.value},device)

    override fun toString(): String = """
        HeartRateMeasurement(
            measurementValue: $measurementValue,
            energyExpended: $energyExpended,
            sensorContact: $sensorContact,
            rrInterval: $rrInterval,
        )
    """.trimIndent()
}

class BodySensorLocationRecord(
    val location : BodySensorLocation,
    device: PeripheralDescription
) : DataRecord(device) {
    override fun toString(): String = "BodySensorLocation: $location"
}

class ControlPointRecord(val response : RecordControlPointResponse, device: PeripheralDescription) : DataRecord(device) {
    override fun toString(): String {
        return response.toString()
    }
}

class GlucoseRecord(
    val unit : BloodGlucoseUnit,
    val amount : GATTValue.SFloat,
    val sequenceNumber : Int,
    val baseTime : GATTValue.DateTime,
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

    /** returns the correct amount if amount is valid, returns -1 if amount is not valid( ex. NaN, out of range, resterved for future use..)**/
    val adjustedAmount : Float
    get() {
        if(amount !is GATTValue.SFloat.Value) return -1f
        return if (unit == BloodGlucoseUnit.DL) amount.value * 100000 else amount.value * 1000
    }

    override fun toString(): String = """
    Glucose Record ( 
    unit: $unit,
    amount: $amount,
    adjustedAmount: $adjustedAmount,
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
            unit : GATTValue.Flag,
            amount : GATTValue.SFloat?,
            sequenceNumber : GATTValue.UInt16,
            baseTime: GATTValue.DateTime,
            timeOffset: GATTValue.SInt16?,
            contextFollows : Boolean,
            sampleType: GATTValue.Nibble?,
            sampleLocation: GATTValue.Nibble?,
            sensorStatusAnunciation: GATTValue.UInt16?,
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
    val mealWeightKg : GATTValue.SFloat?,//int KG
    val mealContext: MealContext?,
    val tester : Tester?,
    val health : Health?,
    val exerciseDuration : Int?,
    val exerciseIntensityPercent : Int?,
    val medicationID: MedicationID?,
    val medicationInKg : GATTValue.SFloat?,
    val medicationInLiter : GATTValue.SFloat?,
    val HbA1cPercent : GATTValue.SFloat?,
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
            sequenceNumber: GATTValue.UInt16,
            carbohydrateType: GATTValue.UInt8?,
            mealWeight: GATTValue.SFloat?,
            mealContext: GATTValue.UInt8?,
            tester: GATTValue.UInt8?,
            health: GATTValue.UInt8?,
            exerciseDuration: GATTValue.UInt16?,
            exerciseIntensityPercent: GATTValue.UInt8?,
            medicationID: GATTValue.UInt8?,
            medicationInKg: GATTValue.SFloat?,
            medicationInLiter: GATTValue.SFloat?,
            HbA1cPercent: GATTValue.SFloat?,
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