package data

sealed class BodySensorLocation {
    object Other : BodySensorLocation()
    object Chest : BodySensorLocation()
    object Wrist : BodySensorLocation()
    object Finger : BodySensorLocation()
    object Hand : BodySensorLocation()
    object EarLobe : BodySensorLocation()
    object Foot : BodySensorLocation()

    override fun toString(): String = this::class.simpleName ?: ""
}