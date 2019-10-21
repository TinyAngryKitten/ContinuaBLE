package bledata

import util.positiveBitAt

//describes a glucse peripherals supported flags
data class GlucoseFeatures(
    val lowBattery : Boolean = false,
    val sensorMalfunction : Boolean = false,
    val sensorSampleSize : Boolean = false,
    val sensorStripInsertionMalfunction : Boolean = false,
    val sensorStripTypeError : Boolean = false,
    val sensorHighLowDetection : Boolean = false,
    val sensorTemperatureHighLowDetection : Boolean = false,
    val sensorReadInterruptedDetection : Boolean = false,
    val generalDeviceFaultDetection : Boolean = false,
    val timeFault : Boolean = false,
    val multipleBonds : Boolean = false
)