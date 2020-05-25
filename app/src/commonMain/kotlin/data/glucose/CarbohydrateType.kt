package data.glucose

sealed class CarbohydrateType(val value : Int) {
    object ReservedForFutureUse : CarbohydrateType(0)
    object Breakfast : CarbohydrateType(1)
    object Lunch : CarbohydrateType(2)
    object Dinner : CarbohydrateType(3)
    object Snack : CarbohydrateType(4)
    object Drink : CarbohydrateType(5)
    object Supper : CarbohydrateType(6)
    object Brunch : CarbohydrateType(7)

    override fun toString(): String = this::class.simpleName ?: ""

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