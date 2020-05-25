package data

class BPMeasurementStatus(
    val bodyMovedDuringMeasurement : Boolean,
    val cuffTooLoose : Boolean,
    val irregularPulseDetected : Boolean,
    val pulseRateIsOverThreshold : Boolean,
    val pulseRateBelowThreshold : Boolean,
    val improperMeasurementPosition : Boolean
) {
    override fun toString(): String = """ 
        MeasurementStatus(
            bodyMovementDuringMeasurement: $bodyMovedDuringMeasurement,
            cuffTooLoose: $cuffTooLoose,
            irregularPulseDetected: $irregularPulseDetected,
            pulseRateIsOverThreshold: $pulseRateIsOverThreshold,
            pulseRateIsUnderThreshold: $pulseRateBelowThreshold,
            improperMesurementPosition: $improperMeasurementPosition
        ),
    """.trimIndent()
}