package data

sealed class TemperatureType {
    object Armpit: TemperatureType()
    object Body : TemperatureType()
    object Ear: TemperatureType()
    object Finger: TemperatureType()
    object GastroIntestinalTract: TemperatureType()
    object Mouth: TemperatureType()
    object Rectum: TemperatureType()
    object Toe: TemperatureType()
    object Tympanum : TemperatureType()
    object ReservedForFutureUse : TemperatureType()

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
        fun fromInt(value: Int) = when(value) {
            1 -> Armpit
            2 -> Body
            3 -> Ear
            4 -> Finger
            5 -> GastroIntestinalTract
            6 -> Mouth
            7 -> Rectum
            8 -> Toe
            9 -> Tympanum
            else -> ReservedForFutureUse
        }
    }
}