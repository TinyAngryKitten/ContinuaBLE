import bledata.BLEReading
import data.*
import gatt.parse
import gatt.services.heightResolution
import gatt.services.weightResolution

fun parseWeightMeasurement(reading : BLEReading) =
    parse(reading) {
        flags(0..0)

        WeightRecord.fromISOValues(
            weight = uint16(),
            weightUnit = if(flag(0)) WeightUnit.LB else WeightUnit.KG,
            timeStamp = onCondition(flag(1),dateTime),
            userId = onCondition(flag(2),uint8),
            BMI = onCondition(flag(3),uint16),
            height = onCondition(flag(3),uint16),
            heightUnit = if(flag(0)) LengthUnit.Inch else LengthUnit.M,
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }   

fun parseWeightScaleFeature(reading : BLEReading) =
    parse(reading) {
        flags(0..3)

        WeightFeatures(
            flag(0),
            flag(1),
            flag(2),
            weightResolution(3),
            heightResolution(7),
            reading.device
        )
    }
