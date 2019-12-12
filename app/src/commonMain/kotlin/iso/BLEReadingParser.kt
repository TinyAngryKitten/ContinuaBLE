package iso

import bledata.BLEReading
import iso.services.parseGlucoseFeatures
import iso.services.parseGlucoseReading
import sample.logger


fun parseBLEReading(reading : BLEReading) {
    when(reading.characteristic.UUID) {
        glucoseFeatureCharacteristic.substring(2) -> parseGlucoseFeatures(
            reading
        )
        glucoseMeasurementCharacteristic.substring(2) -> parseGlucoseReading(
            reading
        )
        glucoseMeasurementContextCharacteristic.substring(2) -> logger.info("Glucose context ignored")

        else -> {
            logger.error("\nERROR: Unknown UUID: "+reading.characteristic.UUID)
        }
    }
}
