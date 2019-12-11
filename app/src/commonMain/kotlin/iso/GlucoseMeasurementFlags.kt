package iso

data class GlucoseMeasurementFlags(
    val timeOffset : Boolean,
    val concentrationTypeAndSample : Boolean,
    val concentrationUnit : Boolean,
    val sensorstatusAnnunciation : Boolean,
    val contextInformationFollows : Boolean

)