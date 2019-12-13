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
)

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
        fun fromISO(
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


//TODO: support this maybe? unknown if any glucose meter actually support it or if it would be useful
class GlucoseRecordContext() : DataRecord() {}