package data.pulseoximeter

data class PulseOximeterMeasurementStatusSupport(
    val measurementOngoing : Boolean,
    val earlyEstimatedData : Boolean,
    val validatedData : Boolean,
    val fullyQualifiedData : Boolean,
    val dataFromMeasurementStorage : Boolean,
    val dataForDemonstration : Boolean,
    val dataForTesting : Boolean,
    val calibrationOngoing : Boolean,
    val measurementUnavailable: Boolean,
    val questionableMeasurementDetected : Boolean,
    val invalidMeasurementDetected : Boolean
)