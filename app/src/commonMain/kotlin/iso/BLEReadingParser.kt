package iso

import bledata.BLEReading
import iso.services.parseGlucoseFeatures
import iso.services.parseGlucoseReading
import sample.logger


fun parseBLEReading(reading : BLEReading) {
    when(reading.characteristic.UUID) {
        CharacteristicUUIDs.glucoseFeature.nr -> parseGlucoseFeatures(reading)
        CharacteristicUUIDs.glucoseMeasurement.nr -> parseGlucoseReading(reading)
        CharacteristicUUIDs.glucoseMeasurementContext.nr -> logger.info("Glucose context ignored")

        else -> {
            logger.error("\nERROR: Unknown UUID: "+reading.characteristic.UUID)
        }
    }
}
