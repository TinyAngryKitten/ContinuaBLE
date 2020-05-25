package data.pulseoximeter

enum class PLXSpotCheckMeasurementStatus {
    MeasurementOngoing,
    EarlyEstimateData,
    ValidatedData,
    FullyQualifiedData,
    DataFromMeasurementStorage,
    DataForDemonstration,
    DataForTesting,
    CalibrationOngoing,
    MeasurementUnavailable,
    QuestionableMeasurementDetected,
    InvalidMeasurementDetecteds,
    ReservedForFutureUse;

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
            fun fromInt(i : Int) : PLXSpotCheckMeasurementStatus = when(i) {
            5 -> MeasurementOngoing
            6 -> EarlyEstimateData
            7-> ValidatedData
            8 -> FullyQualifiedData
            9 -> DataFromMeasurementStorage
            10 -> DataForDemonstration
            11 -> DataForTesting
            12 -> CalibrationOngoing
            13 -> MeasurementUnavailable
            14 -> QuestionableMeasurementDetected
            15 -> InvalidMeasurementDetecteds
            else -> ReservedForFutureUse
        }
    }
}