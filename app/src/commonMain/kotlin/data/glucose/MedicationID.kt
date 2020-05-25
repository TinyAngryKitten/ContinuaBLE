package data.glucose

sealed class MedicationID(val value : Int) {
    object ReservedForFutureUse : MedicationID(0)
    object RapidActingInsulin : MedicationID(1)
    object ShortActingInsuling : MedicationID(2)
    object IntermediateActingInsulin : MedicationID(3)
    object LongActingInsulin : MedicationID(4)
    object PremixedInsulin : MedicationID(5)

    override fun toString(): String = this::class.simpleName ?: ""

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