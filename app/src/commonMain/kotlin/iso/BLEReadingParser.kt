package iso

import bledata.BLEReading
import sample.logger


fun parse(reading : BLEReading) {
    when(reading.characteristic.UUID) {
        glucoseFeatureCharacteristic.substring(2) -> parseGlucoseFeatures(reading)
        glucoseMeasurementCharacteristic.substring(2) -> parseGlucoseReading(reading)
        glucoseMeasurementContextCharacteristic.substring(2) -> logger.info("Glucose context ignored")

        else -> {
            logger.error("\nERROR: Unknown UUID: "+reading.characteristic.UUID)
        }
    }
}
