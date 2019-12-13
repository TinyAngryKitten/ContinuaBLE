package dsl
import data.*

class GlucoseRecordDSL {
    private var unit: BloodGlucoseUnit? = null
    private var amount: Float? = null
    private var glucoseContext: GlucoseRecordContext? = null
    private var nr: UInt? = null

    fun unit(fn: () -> BloodGlucoseUnit) {
        unit = fn()
    }

    fun amount(fn: () -> Float) {
        amount = fn()
    }

    fun context(fn: () -> GlucoseRecordContext) {
        glucoseContext = fn()
    }

    fun sequenceNr(fn: () -> UInt) {
        nr = fn()
    }

    fun build(): DataRecord {
        return GlucoseRecord(
            unit ?: return EmptyRecord,
            amount ?: return EmptyRecord,
            nr ?: return EmptyRecord,
            glucoseContext
        )
    }
}