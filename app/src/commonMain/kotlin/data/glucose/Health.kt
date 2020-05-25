package data.glucose

sealed class Health(val value : Int) {
    object ReservedForFutureUse : Health(0)
    object MinorHealthIssues : Health(1)
    object MajorHealthIssues : Health(2)
    object DuringMenses : Health(3)
    object UnderStress : Health(4)
    object NoHealthIssues : Health(5)
    object NotAvailable : Health(15)

    override fun toString(): String = this::class.simpleName ?: ""

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