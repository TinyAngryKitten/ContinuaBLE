package data.pulseoximeter

data class PulseOximeterSupportedFeatures(
    val measurementStatus: Boolean,
    val deviceAndSensorStatus: Boolean,
    val measurementStoreForSpotCheck : Boolean,
    val timestampForSpotCheck: Boolean,
    val spo2PRFast : Boolean,
    val spo2PRSlow : Boolean,
    val pulseAmplitudeIndexField: Boolean,
    val multipleBonds: Boolean
)