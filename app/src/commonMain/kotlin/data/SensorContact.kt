package data

sealed class SensorContact {
    object NotSupported : SensorContact()
    object ContactNotDetected : SensorContact()
    object ContactDetected : SensorContact()

    override fun toString(): String = this::class.simpleName ?: ""
}