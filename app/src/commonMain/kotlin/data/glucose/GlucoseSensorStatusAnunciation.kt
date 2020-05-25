package data.glucose

enum class GlucoseSensorStatusAnunciation {
    BatteryLowAtMeasurement,
    SensorMalfunctionAtMeasurement,
    SampleSizeOfBloodInsufficient,
    StripInsertionError,
    StripTypeIncorrect,
    SensorResultTooHigh,
    SensorResultTooLow,
    SensorTemperatureTooHigh,
    SensorTemperatureTooLow,
    SensorReadInterrupted,
    GeneralDeviceFault,
    TimeFault_TimeMightBeInaccurate,
    ReservedForFutureUse;

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
        fun fromInt(i : Int) = when(i) {
            0 -> BatteryLowAtMeasurement
            1 -> SensorMalfunctionAtMeasurement
            2 -> SampleSizeOfBloodInsufficient
            3 -> StripInsertionError
            4 -> StripTypeIncorrect
            5 -> SensorResultTooHigh
            6 -> SensorResultTooLow
            7 -> SensorTemperatureTooHigh
            8 -> SensorTemperatureTooLow
            9 -> SensorReadInterrupted
            10 -> GeneralDeviceFault
            11 -> TimeFault_TimeMightBeInaccurate
            else -> ReservedForFutureUse

        }
    }
}