package data.glucose

sealed class MealContext(val value : Int) {
    object ReservedForFutureUse : MealContext(0)
    object Preprandial : MealContext(1)
    object Postprandial : MealContext(2)
    object Fasting : MealContext(3)
    object Casual : MealContext(4) // casual drink / snack etc.
    object Bedtime : MealContext(5)

    override fun toString(): String = this::class.simpleName ?: ""

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