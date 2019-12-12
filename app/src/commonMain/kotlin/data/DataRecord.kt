package data

import iso.ISOValue


sealed class DataRecord

object EmptyRecord : DataRecord()

data class GlucoseRecord(
    val unit : BloodGlucoseMeasurement,
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
                if(unit.value) BloodGlucoseMeasurement.MMOL else BloodGlucoseMeasurement.DL,
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



//TODO: support this maybe? unknown if any glucose meter actually support it or if it would be useful
class GlucoseRecordContext() : DataRecord() {}

