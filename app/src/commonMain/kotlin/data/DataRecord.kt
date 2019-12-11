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

class GlucoseRecordContext() : DataRecord()
