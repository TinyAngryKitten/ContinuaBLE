import bledata.BLEReading
import data.HeightMeasurementResolution
import data.WeightFeatures
import data.WeightMeasurementResolution
import data.WeightRecord
import iso.parse

fun weightServiceParser(reading : BLEReading) =
    parse(reading.data) {
        flags(0..1)

        WeightRecord(

        )
    }

fun weightScaleFeatureParser(reading : BLEReading) =
    parse(reading.data) {
        flags(0..4)

        WeightFeatures(
            flag(0),
            flag(1),
            flag(2),
            WeightMeasurementResolution.fromInt(
                       flag(3) * 1
                        + flag(4) * 2
                        + flag(5) * 4
                        + flag(6) * 8
            ),
            HeightMeasurementResolution.fromInt(
                flag(7) * 1
                + flag(8) * 2
                + flag(9) * 2
            )
        )
    }

private operator fun Boolean.times(i : Int) = if(this) i else 0