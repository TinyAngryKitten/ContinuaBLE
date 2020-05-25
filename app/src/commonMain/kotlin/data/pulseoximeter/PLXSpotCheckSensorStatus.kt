package data.pulseoximeter

enum class PLXSpotCheckSensorStatus {
    ExtendedDisplayUpdateOngoing,
    EquipmentMalfunctionDetected,
    SignalProcessingIrregularityDetected,
    InadequateSignalDetected,
    PoorSignalDetected,
    LowPerfusionDetected,
    ErraticSignalDetected,
    NonPulsatileSignalDetected,
    QuestionablePulseDetected,
    SignalAnalysisOngoing,
    SensorInterfaceDetected,
    SensorUnconnectedToUser,
    UnknownSensorConnected,
    SensorDisplaced,
    SensorMalfunction,
    SensorDisconnected,
    ReservedForFutureUse;

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
        fun fromInt(i: Int) = when (i) {
            0 -> ExtendedDisplayUpdateOngoing
            1 -> EquipmentMalfunctionDetected
            2 -> SignalProcessingIrregularityDetected
            3 -> InadequateSignalDetected
            4 -> PoorSignalDetected
            5 -> LowPerfusionDetected
            6 -> ErraticSignalDetected
            7 -> NonPulsatileSignalDetected
            8 -> QuestionablePulseDetected
            9 -> SignalAnalysisOngoing
            10 -> SensorInterfaceDetected
            11 -> SensorUnconnectedToUser
            12 -> UnknownSensorConnected
            13 -> SensorDisplaced
            14 -> SensorMalfunction
            15 -> SensorDisconnected
            else -> ReservedForFutureUse
        }
    }
}