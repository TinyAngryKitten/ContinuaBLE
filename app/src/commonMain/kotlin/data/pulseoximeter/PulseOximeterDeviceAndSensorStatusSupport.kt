package data.pulseoximeter

data class PulseOximeterDeviceAndSensorStatusSupport(
    val extendedDisplayUpdateOngoing : Boolean,
    val equipmentMalfunctionDetected : Boolean,
    val signalProcessingIrregularityDetected : Boolean,
    val inadequateSignalDetected : Boolean,
    val poorSignalDetected : Boolean,
    val lowPerfusionDetected : Boolean,
    val erraticSignalDetected : Boolean,
    val nonPulseatileSignalDetected : Boolean,
    val questionablePulseDetected : Boolean,
    val signalAnalysisOngoing : Boolean,
    val sensorInterfaceDetected : Boolean,
    val sensorUnconnectedToUser : Boolean,
    val unknownSensorConnected : Boolean,
    val sensorDisplaced : Boolean,
    val sensorMalfunction : Boolean,
    val sensorDisconnected : Boolean
)