package data.glucose

sealed class Tester(val value : Int) {
    object ReservedForFutureUse : Tester(0)
    object Self : Tester(1)
    object HealthCareProfessional : Tester(2)
    object LabTest : Tester(3)
    object NotAvailable : Tester(15)

    override fun toString(): String = this::class.simpleName ?: ""

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